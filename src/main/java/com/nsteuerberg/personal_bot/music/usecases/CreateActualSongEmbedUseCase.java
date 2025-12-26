package com.nsteuerberg.personal_bot.music.usecases;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;

import static com.nsteuerberg.personal_bot.utils.CommonUtils.*;

@Component
public class CreateActualSongEmbedUseCase {
    public MessageEmbed execute(AudioTrack track) {

        return new EmbedBuilder()
                .setTitle("Reproduciendo")
                .setThumbnail(getMusicThumbnail(track))
                .addField("Title", track.getInfo().title, false)
                .addField("Author", track.getInfo().author, false)
                .addField("Duration", formatDuration(track.getDuration()), false)
                .addField("Progress", getProgressBar(track), false)
                .build();
    }
}
