package com.nsteuerberg.personal_bot.music.commands;

import com.nsteuerberg.personal_bot.commands.interfaces.IMusicCommand;
import com.nsteuerberg.personal_bot.configuration.CustomName;
import com.nsteuerberg.personal_bot.music.exceptions.PlayException;
import com.nsteuerberg.personal_bot.music.service.MusicMessageService;
import com.nsteuerberg.personal_bot.music.service.MusicService;
import com.nsteuerberg.personal_bot.music.service.audio.GuildMusicHandler;
import com.nsteuerberg.personal_bot.music.usecases.CreateActualSongEmbedUseCase;
import com.nsteuerberg.personal_bot.music.usecases.CreateAddSongEmbedUseCase;
import com.nsteuerberg.personal_bot.music.usecases.SearchTrackUseCase;
import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@CustomName(CommandConstants.PLAY)
@Component
public class PlayCommand implements IMusicCommand {
    private final SearchTrackUseCase searchTrackUseCase;
    private final AudioPlayerManager playerManager;
    private final MusicService musicService;
    private final CreateActualSongEmbedUseCase createActualSongEmbedUseCase;
    private final CreateAddSongEmbedUseCase createAddSongEmbedUseCase;

    public PlayCommand(SearchTrackUseCase searchTrackUseCase, AudioPlayerManager playerManager, MusicService musicService, CreateActualSongEmbedUseCase createActualSongEmbedUseCase, CreateAddSongEmbedUseCase createAddSongEmbedUseCase) {
        this.searchTrackUseCase = searchTrackUseCase;
        this.playerManager = playerManager;
        this.musicService = musicService;
        this.createActualSongEmbedUseCase = createActualSongEmbedUseCase;
        this.createAddSongEmbedUseCase = createAddSongEmbedUseCase;
    }

    private final Logger logger = LoggerFactory.getLogger(PlayCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            String song = event.getOption("song").getAsString();
            logger.info("Agregando {} a la playlist de {}", song, event.getGuild().getName());

            GuildVoiceState memberVoiceState = event.getMember().getVoiceState();
            if (!memberVoiceState.inAudioChannel())
                throw new PlayException("Para reproducir/agregar canciones necesitas estar en un canal de voz");

            GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();
            if (!selfVoiceState.inAudioChannel())
                connectToChannel(event.getChannel().asTextChannel(), memberVoiceState, event.getGuild());
            else if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel()))
                throw new PlayException("Ya estoy conectado en este servidor, conectate al mismo canal para poder reproducir/agregar canciones");

            play(event.getGuild().getId(), song);
            event.reply("Procesan").setEphemeral(true).queue(msg ->
                msg.deleteOriginal().queueAfter(1, TimeUnit.SECONDS)
            );
        } catch (PlayException e) {
            logger.warn(e.getMessage());
            event.reply(e.getMessage()).queue();
        }catch (Exception e) {
            logger.error(e.getMessage());
            event.reply("Error reproduciendo/agregando la canción, sentimos las molestias").queue();
        }
    }

    private void play(String guildId, String query) throws Exception {
        GuildMusicHandler musicHandler = musicService.getMusicHandler(guildId);
        if (musicHandler==null)
            throw new Exception("GuildMusicHandler de la guild %s no deberia ser nulo".formatted(guildId));
        searchTrackUseCase.execute(query, 25)
                .thenAccept(musicHandler::addTrack)
                .exceptionally(ex -> {
                    logger.error(ex.getMessage());
                   return null;
                });

    }

    private void connectToChannel(TextChannel textChannel, GuildVoiceState memberVoiceState, Guild guild) {
        AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(memberVoiceState.getChannel());
        GuildMusicHandler guildMusicHandler = new GuildMusicHandler(playerManager, audioManager, () -> musicService.deleteMusicHandler(guild.getId()));
        guildMusicHandler.addListener(new MusicMessageService(textChannel, createActualSongEmbedUseCase, createAddSongEmbedUseCase));
        musicService.addMusicHandler(guild.getId(), guildMusicHandler);
    }
}
