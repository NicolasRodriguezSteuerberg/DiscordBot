package com.nsteuerberg.personal_bot.music.usecases;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import org.springframework.stereotype.Component;

import static com.nsteuerberg.personal_bot.utils.CommonUtils.formatDuration;

@Component
public class AutoCompleteSongUseCase {
    private final SearchTrackUseCase searchTrackUseCase;

    public AutoCompleteSongUseCase(SearchTrackUseCase searchTrackUseCase) {
        this.searchTrackUseCase = searchTrackUseCase;
    }

    public void execute(CommandAutoCompleteInteraction event) {
        String query = event.getOption("song").getAsString();
        searchTrackUseCase.execute(query,25)
                .thenAccept(tracks ->
                    event.replyChoices(
                        tracks.stream()
                                .map(AudioTrack::getInfo)
                                .map(this::getCommand)
                                .toList()
                    ).queue()
                ).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private Command.Choice getCommand(AudioTrackInfo trackInfo) {
        String duration = "%s".formatted(formatDuration(trackInfo.length));
        String author = "%s".formatted(trackInfo.author);
        String title = trackInfo.title;

        // mas 6 por el ' -  []'
        int maxTitleLength = 100 - (author.length() + duration.length() + 6);

        if (title.length() > maxTitleLength) {
            title = title.substring(0, maxTitleLength - 3) + "...";
        }
        String name = "%s - %s [%s]".formatted(title, author, duration);

        return new Command.Choice(
                name, trackInfo.uri
        );
    }
}
