package com.nsteuerberg.personal_bot.commands.interfaces;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public interface IMusicCommand extends ICommand{
    void onAutoComplete(CommandAutoCompleteInteractionEvent event);
}
