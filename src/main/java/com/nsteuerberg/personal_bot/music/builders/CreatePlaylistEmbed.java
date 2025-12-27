package com.nsteuerberg.personal_bot.music.builders;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.nsteuerberg.personal_bot.utils.CommonUtils.*;

@Component
public class CreatePlaylistEmbed {
    public Optional<MessageEmbed> execute(AudioTrack actualTrack, List<AudioTrack> playlist, int page, int size) {
        int start = page * size;
        if (actualTrack == null && (playlist.isEmpty() || start > playlist.size() - 1))
            return Optional.empty();
        int end = Math.min(start + size, playlist.size());

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Playlist %d/%d".formatted(page + 1, getMaxPages(playlist, size)))
                .setThumbnail(getMusicThumbnail(actualTrack));

        String description = "## Playing\n%s\n%s\n".formatted(getContent(actualTrack.getInfo()), getProgressBar(actualTrack));

        while (start < end) {
            AudioTrack track = playlist.get(start);
            start++;
            builder.addField(
                "%d. %s".formatted(start, track.getInfo().title),
                "%s [%s]".formatted(track.getInfo().author, formatDuration(track.getDuration())),
                false
            );
        }

        return Optional.of(builder.build());
    }

    private int getMaxPages(List<?> tracks, int sizeByPage) {
        return (int) Math.ceil((double) tracks.size() / sizeByPage);
    }

    private String getContent(AudioTrackInfo info) {
        return "**%s** - %s [%s]".formatted(info.title, info.author, formatDuration(info.length));
    }

}
