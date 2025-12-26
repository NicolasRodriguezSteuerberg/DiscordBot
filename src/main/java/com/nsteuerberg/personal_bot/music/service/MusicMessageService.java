package com.nsteuerberg.personal_bot.music.service;

import com.nsteuerberg.personal_bot.music.listener.MusicEventListener;
import com.nsteuerberg.personal_bot.music.usecases.CreateActualSongEmbedUseCase;
import com.nsteuerberg.personal_bot.music.usecases.CreateAddSongEmbedUseCase;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class MusicMessageService implements MusicEventListener {
    private final TextChannel channel;
    private final CreateActualSongEmbedUseCase createActualSongEmbedUseCase;
    private final CreateAddSongEmbedUseCase createAddSongEmbedUseCase;

    private Message nowPlayingMessage;

    public MusicMessageService(TextChannel channel, CreateActualSongEmbedUseCase createActualSongEmbedUseCase, CreateAddSongEmbedUseCase createAddSongEmbedUseCase) {
        this.channel = channel;
        this.createActualSongEmbedUseCase = createActualSongEmbedUseCase;
        this.createAddSongEmbedUseCase = createAddSongEmbedUseCase;
    }

    @Override
    public void trackAdded(AudioTrack track, int position) {
        MessageEmbed embed = createAddSongEmbedUseCase.execute(track, position);
        channel.sendMessageEmbeds(embed).queue();
    }

    @Override
    public void trackStart(AudioTrack track) {
        MessageEmbed embed = createActualSongEmbedUseCase.execute(track);
        if (embed == null) return;
        if (nowPlayingMessage == null)
            sendNowPlayingMessage(embed);
        else
            nowPlayingMessage.editMessageEmbeds(embed).queue(null, failure -> {
                sendNowPlayingMessage(embed);
            });
    }

    private void sendNowPlayingMessage(MessageEmbed embed) {
        channel.sendMessageEmbeds(embed).queue(msg -> nowPlayingMessage = msg);
    }

    @Override
    public void playlistEmpty() {
        if (nowPlayingMessage == null) return;
        nowPlayingMessage.editMessage("No hay mas canciones a reproducir").queue();
    }

    @Override
    public void trackError(AudioTrack track, String exception) {
        channel.sendMessage("Error reproduciendo %s".formatted(track.getInfo().title)).queue();
    }
}
