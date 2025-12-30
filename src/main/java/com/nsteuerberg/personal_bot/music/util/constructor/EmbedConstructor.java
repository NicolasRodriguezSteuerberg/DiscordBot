package com.nsteuerberg.personal_bot.music.util.constructor;

import com.nsteuerberg.personal_bot.music.audio.model.TrackEntry;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;

import static com.nsteuerberg.personal_bot.utils.CommonUtils.formatDuration;
import static com.nsteuerberg.personal_bot.utils.CommonUtils.getMusicThumbnail;

public class EmbedConstructor {

    public static MessageEmbed createCurrentTrackEmbed(AudioTrack track) {
        TrackEntry entry = (TrackEntry) track.getUserData();
        AudioTrackInfo info = track.getInfo();
        String description = """
        ## [%s - %s](%s) - `%s`
        > Requested by <@%s>
        """.formatted(
                info.title, info.author, info.uri, formatDuration(info.length),
                entry.getAddedByUserId()
        );

        return new EmbedBuilder()
                .setTitle("Now playing")
                .setDescription(description)
                .setColor(Color.CYAN)
                .setThumbnail(getMusicThumbnail(track))
                .build();
    }

    public static MessageEmbed createPlaylistEmbed(int page, int size, List<AudioTrack> tracks, AudioTrack currentTrack) {
        int start = page * size;
        if (start > tracks.size() - 1) return null;
        int end = Math.min(start + size, tracks.size());
        int maxPages = (int) Math.ceil((double) tracks.size() / size);

        AudioTrackInfo currentTrackInfo = currentTrack.getInfo();
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Playlist %d/%d".formatted(page + 1, maxPages))
                .setDescription("## Now playing\n[%s - %s](%s) - `%s`".formatted(
                        currentTrackInfo.title, currentTrackInfo.author, currentTrackInfo.uri, formatDuration(currentTrackInfo.length)
                ))
                .setThumbnail(getMusicThumbnail(currentTrack))
                .setColor(Color.MAGENTA);
        String description = """
        ## Now playing\n[%s - %s](%s) - `%s`
        
        ## Playlist
        """.formatted(currentTrackInfo.title, currentTrackInfo.author, currentTrackInfo.uri, formatDuration(currentTrackInfo.length));

        while (start < end) {
            AudioTrackInfo trackInfo = tracks.get(start).getInfo();
            // sumamos start antes para tener el indice visual
            start ++;
            description += "\n%d. [%s](%s) - %s `%s`".formatted(start, trackInfo.title, trackInfo.uri, trackInfo.author, formatDuration(trackInfo.length));
        }

        builder.setDescription(description);

        return builder.build();
    }
}
