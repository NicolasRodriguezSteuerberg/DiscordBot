package com.nsteuerberg.personal_bot.music.commands;

import com.nsteuerberg.personal_bot.commands.interfaces.IMusicCommand;
import com.nsteuerberg.personal_bot.configuration.CustomName;
import com.nsteuerberg.personal_bot.music.service.MusicService;
import com.nsteuerberg.personal_bot.music.service.audio.GuildMusicHandler;
import com.nsteuerberg.personal_bot.music.usecases.CreateActualSongEmbedUseCase;
import com.nsteuerberg.personal_bot.music.usecases.CreatePlaylistEmbedUseCase;
import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@CustomName(CommandConstants.PLAYLIST)
@Component
public class PlaylistCommand implements IMusicCommand {
    private final MusicService musicService;
    private final CreatePlaylistEmbedUseCase createPlaylistEmbedUseCase;
    private final CreateActualSongEmbedUseCase createActualSongEmbedUseCase;

    public PlaylistCommand(MusicService musicService, CreatePlaylistEmbedUseCase createPlaylistEmbedUseCase, CreateActualSongEmbedUseCase createActualSongEmbedUseCase) {
        this.musicService = musicService;
        this.createPlaylistEmbedUseCase = createPlaylistEmbedUseCase;
        this.createActualSongEmbedUseCase = createActualSongEmbedUseCase;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        GuildMusicHandler musicHandler = musicService.getMusicHandler(event.getGuild().getId());
        if (musicHandler == null){
            System.out.println("sin music handler");
            event.reply("No hay playlist").queue();
            return;
        }
        try {
            List<AudioTrack> playlist = musicHandler.getPlaylist();
            AudioTrack actualSong = musicHandler.getActualSong();
            MessageEmbed embed;
            if (actualSong != null && playlist.isEmpty()){
                embed = createActualSongEmbedUseCase.execute(actualSong);
            } else {
                embed = createPlaylistEmbedUseCase.execute(actualSong, playlist, 0, 10)
                        .orElseThrow(() -> new Exception("Error construyendo la playlist"));
            }

            event.replyEmbeds(embed).queue();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            event.reply("No hay playlist").queue();
        }
    }
}
