package com.nsteuerberg.personal_bot.music.util.constructor;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.interactions.commands.Command;

import static com.nsteuerberg.personal_bot.utils.CommonUtils.formatDuration;

public class CommandChoiceConstructor {

    public static Command.Choice getAutoCompleteChoice(AudioTrackInfo info) {
        String duration = "%s".formatted(formatDuration(info.length));
        String author = "%s".formatted(info.author);
        String title = info.title;

        // mas 6 por el ' -  []'
        int maxTitleLength = 100 - (author.length() + duration.length() + 6);

        if (title.length() > maxTitleLength) {
            title = title.substring(0, maxTitleLength - 3) + "...";
        }
        String name = "%s - %s [%s]".formatted(title, author, duration);

        return new Command.Choice(
                name, info.uri
        );
    }
}
