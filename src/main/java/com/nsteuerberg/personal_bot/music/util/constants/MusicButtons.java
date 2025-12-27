package com.nsteuerberg.personal_bot.music.util.constants;

import lombok.Getter;

@Getter
public enum MusicButtons {
    PLAY(
        "music_play",
        "▶\uFE0F Play"
    ),
    PAUSE(
        "music_pause",
        "⏸\uFE0F Pause"
    ),
    NEXT(
        "music_next",
        "⏭\uFE0F Next"
    ),
    STOP(
        "music_stop",
        "⏹\uFE0F Stop"
    ),
    NEXT_PAGE(
        "music_page_next:",
        "▶\uFE0F"
    ),
    PREV_PAGE(
        "music_page_prev:",
        "◀\uFE0F"
    );

    private String id;
    private String label;
    MusicButtons(String id, String label) {
        this.id = id;
        this.label = label;
    }

}
