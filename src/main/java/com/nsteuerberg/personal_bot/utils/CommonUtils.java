package com.nsteuerberg.personal_bot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class CommonUtils {
    public static Color getRandomColor() {
        return new Color(
                ThreadLocalRandom.current().nextInt(256), // Componente R
                ThreadLocalRandom.current().nextInt(256), // Componente G
                ThreadLocalRandom.current().nextInt(256)  // Componente B
        );
    }
    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;
        return hours > 0
                ? "%d:%02d:%02d".formatted(hours, minutes, seconds)
                : "%02d:%02d".formatted(minutes, seconds);
    }

    public static String getProgressBar(AudioTrack track) {
        double progress = Math.max(0.0, Math.min((double) track.getPosition() / track.getDuration(), 1.0));

        int filled = (int) (progress * 10);
        return ":green_square:".repeat(filled) + ":black_large_square:".repeat(10 - filled);
    }

    public static String getMusicThumbnail(AudioTrack track) {
        return track.getInfo().artworkUrl == null
                ? "https://img.youtube.com/vi/" + track.getIdentifier() + "/hqdefault.jpg"
                : track.getInfo().artworkUrl;

    }
}
