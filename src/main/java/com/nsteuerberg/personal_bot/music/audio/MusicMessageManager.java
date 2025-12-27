package com.nsteuerberg.personal_bot.music.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

import static com.nsteuerberg.personal_bot.music.util.constructor.ButtonConstructor.createCurrentTrackButtons;
import static com.nsteuerberg.personal_bot.music.util.constructor.EmbedConstructor.createCurrentTrackEmbed;

public class MusicMessageManager {
    private final TextChannel channel;

    private Message nowPlayingMessage;

    public MusicMessageManager(TextChannel channel) {
        this.channel = channel;
    }

    private void deleteNowPlayingMsg() {
        if (nowPlayingMessage == null) return;
        nowPlayingMessage.delete().queue();
        nowPlayingMessage = null;
    }

    private void sendCurrentTrackMessage(AudioTrack track) {
        MessageEmbed embed = createCurrentTrackEmbed(track);
        List<Button> buttons = createCurrentTrackButtons(track);
        if (nowPlayingMessage == null)
            channel.sendMessageEmbeds(embed)
                    .setActionRow(buttons)
                    .queue(msg -> nowPlayingMessage = msg);
        else
            nowPlayingMessage.editMessageEmbeds(embed)
                    .setActionRow(buttons)
                    .queue();
    }

    public void onStartTrack(AudioTrack track) {
        System.out.println("on start");
        deleteNowPlayingMsg();
        sendCurrentTrackMessage(track);
    }

    public void onPlayPause(AudioTrack track) {
        sendCurrentTrackMessage(track);
    }

    public void onAddedTrack(AudioTrack track, int size) {
        return;
    }

    public void onQueueEmpty() {
        if(nowPlayingMessage != null) deleteNowPlayingMsg();
    }

    public void onError(AudioTrack track, String exception) {

    }
}
