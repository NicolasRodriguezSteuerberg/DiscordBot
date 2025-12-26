package com.nsteuerberg.personal_bot.music.usecases;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;

import static com.nsteuerberg.personal_bot.utils.CommonUtils.*;

@Component
public class CreateAddSongEmbedUseCase {
    public MessageEmbed execute(AudioTrack track, int position) {

        return new EmbedBuilder()
                .setTitle("Agregada canción en la posicion %s".formatted(position))
                .setThumbnail(getMusicThumbnail(track))
                .addField("Title", track.getInfo().title, false)
                .addField("Author", track.getInfo().author, false)
                .addField("Duration", formatDuration(track.getDuration()), false)
                .build();
    }
}
