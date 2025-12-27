package com.nsteuerberg.personal_bot.music.commands;

import com.nsteuerberg.personal_bot.commands.interfaces.IMusicCommand;
import com.nsteuerberg.personal_bot.configuration.CustomName;
import com.nsteuerberg.personal_bot.music.audio.TrackSearchService;
import com.nsteuerberg.personal_bot.music.audio.model.GuildMusicManager;
import com.nsteuerberg.personal_bot.music.exceptions.PlayException;
import com.nsteuerberg.personal_bot.music.service.MusicService;
import com.nsteuerberg.personal_bot.music.util.constructor.CommandChoiceConstructor;
import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@CustomName(CommandConstants.PLAY)
@Component
public class PlayCommand implements IMusicCommand {
    private final AudioPlayerManager playerManager;
    private final MusicService musicService;
    private final TrackSearchService searchService;

    public PlayCommand(AudioPlayerManager playerManager, MusicService musicService, TrackSearchService searchService) {
        this.playerManager = playerManager;
        this.musicService = musicService;
        this.searchService = searchService;
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

            play(event.getGuild().getId(), event.getUser().getId(), song);
            event.reply("Procesando...").setEphemeral(true).queue(msg ->
                msg.deleteOriginal().queueAfter(100, TimeUnit.MILLISECONDS)
            );
        } catch (PlayException e) {
            logger.warn(e.getMessage());
            event.reply(e.getMessage()).queue();
        }catch (Exception e) {
            logger.error(e.getMessage());
            event.reply("Error reproduciendo/agregando la canción, sentimos las molestias").queue();
        }
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        String query = event.getOption("song").getAsString();
        searchService.getAudioTracks(query, 25)
                .thenAccept(tracks ->
                    event.replyChoices(
                        tracks.stream()
                                .map(AudioTrack::getInfo)
                                .map(CommandChoiceConstructor::getAutoCompleteChoice)
                                .toList()
                    ).queue()
                ).exceptionally(ex -> {
                    logger.info(ex.getMessage());
                    event.replyChoices(List.of()).queue();
                    return null ;
                });
    }

    private void play(String guildId, String userId, String query) throws Exception {
        GuildMusicManager manager = musicService.getMusicManager(guildId);
        if (manager==null)
            throw new Exception("GuildMusicHandler de la guild %s no deberia ser nulo".formatted(guildId));
        searchService.getAudioTracks(query, 25)
                .thenAccept(tracks -> manager.addTrack(tracks, userId))
                .exceptionally(ex -> {
                    logger.error(ex.getMessage());
                   return null;
                });

    }

    private void connectToChannel(TextChannel textChannel, GuildVoiceState memberVoiceState, Guild guild) {
        AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(memberVoiceState.getChannel());
        GuildMusicManager guildMusicManager = new GuildMusicManager(playerManager, audioManager, textChannel, () -> musicService.deleteMusicHandler(guild.getId()));
        musicService.addMusicHandler(guild.getId(), guildMusicManager);
    }
}
