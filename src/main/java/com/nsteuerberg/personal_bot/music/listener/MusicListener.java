package com.nsteuerberg.personal_bot.music.listener;

import com.nsteuerberg.personal_bot.commands.interfaces.IMusicCommand;
import com.nsteuerberg.personal_bot.music.audio.model.GuildMusicManager;
import com.nsteuerberg.personal_bot.music.service.MusicService;
import com.nsteuerberg.personal_bot.music.util.constants.MusicButtons;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.nsteuerberg.personal_bot.music.util.constructor.ButtonConstructor.createPlaylistButtons;
import static com.nsteuerberg.personal_bot.music.util.constructor.EmbedConstructor.createPlaylistEmbed;

@Component
public class MusicListener extends ListenerAdapter {
    private final MusicService musicService;
    private final Map<String, IMusicCommand> commandMap;

    public MusicListener(MusicService musicService, Map<String, IMusicCommand> commandMap) {
        this.musicService = musicService;
        this.commandMap = commandMap;
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        IMusicCommand command = commandMap.get(event.getName());
        if (command == null) return;
        command.onAutoComplete(event);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        IMusicCommand command = commandMap.get(event.getName());
        if (command == null) return;
        command.execute(event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();
        if (!buttonId.startsWith("music")) return;

        GuildMusicManager manager = musicService.getMusicManager(event.getGuild().getId());
        if (manager == null) event.reply("Ya no existen datos").setEphemeral(true);

        if (buttonId.equals(MusicButtons.PLAY.getId())) {
            manager.playPause(false);
            event.reply("Procesando...").setEphemeral(true).queue(msg ->
                    msg.deleteOriginal().queueAfter(50, TimeUnit.MILLISECONDS)
            );
        } else if (buttonId.equals(MusicButtons.PAUSE.getId())) {
            manager.playPause(true);
            event.reply("Procesando...").setEphemeral(true).queue(msg ->
                    msg.deleteOriginal().queue()
            );
        } else if (buttonId.equals(MusicButtons.NEXT.getId())) {
            manager.skip();
            event.reply("Procesando...").setEphemeral(true).queue(msg ->
                    msg.deleteOriginal().queue()
            );
        } else if (buttonId.equals(MusicButtons.STOP.getId())) {
            manager.stop();
            event.reply("Procesando...").setEphemeral(true).queue(msg ->
                    msg.deleteOriginal().queue()
            );
        } else if (buttonId.startsWith(MusicButtons.NEXT_PAGE.getId()) ||
                buttonId.startsWith(MusicButtons.PREV_PAGE.getId())
        ) {
            try {
                int page = Integer.parseInt(buttonId.substring(MusicButtons.NEXT_PAGE.getId().length()));
                List<AudioTrack> tracks = manager.getQueue();
                AudioTrack actualTrack = manager.getCurrentSong();
                MessageEmbed embed = createPlaylistEmbed(page, 10, tracks, actualTrack);
                List<Button> buttons = createPlaylistButtons(page, 10, tracks.size());
                event.editMessageEmbeds(embed).setActionRow(buttons).queue();
            } catch (Exception e) {
                event.reply("Error").queue();
            }
        }
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (!event.getMember().getUser().isBot() || !event.getMember().getId().equals(event.getJDA().getSelfUser().getId())) return;
        AudioChannel old = event.getOldValue();
        AudioChannel actual = event.getNewValue();

        if (actual != null) return;
        GuildMusicManager manager = musicService.getMusicManager(event.getGuild().getId());
        if (manager == null) return;
        manager.stop();
    }
}
