package com.nsteuerberg.personal_bot.commands.interfaces;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ICommand {
    void execute(SlashCommandInteractionEvent event);

}
