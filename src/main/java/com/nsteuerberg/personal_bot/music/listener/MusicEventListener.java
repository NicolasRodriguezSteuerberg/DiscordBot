package com.nsteuerberg.personal_bot.music.listener;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public interface MusicEventListener {
    void trackAdded(AudioTrack track, int position);
    void trackStart(AudioTrack track);
    void playlistEmpty();
    void trackError(AudioTrack track, String exception);
}
