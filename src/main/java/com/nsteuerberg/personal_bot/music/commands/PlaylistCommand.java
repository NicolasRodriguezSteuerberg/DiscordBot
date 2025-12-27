package com.nsteuerberg.personal_bot.music.commands;

import com.nsteuerberg.personal_bot.commands.interfaces.IMusicCommand;
import com.nsteuerberg.personal_bot.configuration.CustomName;
import com.nsteuerberg.personal_bot.music.audio.model.GuildMusicManager;
import com.nsteuerberg.personal_bot.music.service.MusicService;
import com.nsteuerberg.personal_bot.music.builders.CreatePlaylistEmbed;
import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.nsteuerberg.personal_bot.music.util.constructor.ButtonConstructor.createPlaylistButtons;
import static com.nsteuerberg.personal_bot.music.util.constructor.EmbedConstructor.createCurrentTrackEmbed;
import static com.nsteuerberg.personal_bot.music.util.constructor.EmbedConstructor.createPlaylistEmbed;

@CustomName(CommandConstants.PLAYLIST)
@Component
public class PlaylistCommand implements IMusicCommand {
    private final MusicService musicService;

    public PlaylistCommand(MusicService musicService, CreatePlaylistEmbed createPlaylistEmbed) {
        this.musicService = musicService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = musicService.getMusicManager(event.getGuild().getId());
        try {
            if (musicManager == null) throw new Exception("No hay handler");
            List<AudioTrack> queue = musicManager.getQueue();
            AudioTrack currentTrack = musicManager.getCurrentSong();
            MessageEmbed embed = getEmbed(queue,currentTrack);
            if (embed == null) throw new Exception("No hay playlist");
            List<Button> buttons = getButtons(queue, currentTrack);

            event.replyEmbeds(embed).setActionRow(buttons).queue();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            event.reply("No hay playlist").queue();
        }
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        return;
    }

    private MessageEmbed getEmbed(List<AudioTrack> queue, AudioTrack current) {
        if (current == null && queue.isEmpty()) return null;
        else if (current != null && queue.isEmpty()) return createCurrentTrackEmbed(current);
        else return createPlaylistEmbed(0, 10, queue, current);
    }

    private List<Button> getButtons(List<AudioTrack> queue, AudioTrack current) {
        if (current != null && queue.isEmpty()) return List.of();
        else return createPlaylistButtons(0, 10, queue.size());
    }
}
