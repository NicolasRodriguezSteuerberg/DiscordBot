package com.nsteuerberg.personal_bot.music.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public class AudioHandler implements AudioSendHandler {
    private final AudioPlayer player;
    private final ByteBuffer buffer;
    private final MutableAudioFrame frame;

    public AudioHandler(AudioPlayer player) {
        this.player = player;
        buffer = ByteBuffer.allocate(1024);
        frame = new MutableAudioFrame(buffer);
    }

    @Override
    public boolean canProvide() {
        return player.provide(frame);
    }

    @Override
    public @Nullable ByteBuffer provide20MsAudio() {
        return buffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
