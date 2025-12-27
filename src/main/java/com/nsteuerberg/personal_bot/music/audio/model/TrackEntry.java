package com.nsteuerberg.personal_bot.music.audio.model;

public class TrackEntry {
    private final String addedByUserId;
    private boolean isPlaying;

    public TrackEntry(String addedByUserId, boolean isPlaying) {
        this.addedByUserId = addedByUserId;
        this.isPlaying = isPlaying;
    }

    public String getAddedByUserId() {
        return addedByUserId;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying() {
        isPlaying = true;
    }

    public void setPause() {
        isPlaying = false;
    }
}
