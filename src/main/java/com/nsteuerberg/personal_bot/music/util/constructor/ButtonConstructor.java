package com.nsteuerberg.personal_bot.music.util.constructor;

import com.nsteuerberg.personal_bot.music.audio.model.TrackEntry;
import com.nsteuerberg.personal_bot.music.util.constants.MusicButtons;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;

public class ButtonConstructor {
    public static List<Button> createCurrentTrackButtons(AudioTrack track) {
        TrackEntry entry = (TrackEntry) track.getUserData();
        return List.of(
                entry.isPlaying()
                    ? Button.primary(MusicButtons.PAUSE.getId(), MusicButtons.PAUSE.getLabel())
                    : Button.success(MusicButtons.PLAY.getId(), MusicButtons.PLAY.getLabel()),
                Button.secondary(MusicButtons.NEXT.getId(), MusicButtons.NEXT.getLabel()),
                Button.danger(MusicButtons.STOP.getId(), MusicButtons.STOP.getLabel())
        );
    }

    public static List<Button> createPlaylistButtons(int page, int size, int trackSize) {
        int maxPages = (int) Math.ceil((double) trackSize / size);
        List<Button> buttons = new ArrayList<>();
        Button prev = Button.primary(MusicButtons.PREV_PAGE.getId() + (page - 1), MusicButtons.PREV_PAGE.getLabel());
        if (page == 0)
            prev = prev.asDisabled();
        Button next = Button.primary(MusicButtons.NEXT_PAGE.getId() + (page + 1), MusicButtons.NEXT_PAGE.getLabel());
        if (maxPages == page - 1)
            next = next.asDisabled();
        return List.of(prev, next);
    }
}
