package com.nsteuerberg.personal_bot.commands;

import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AddCommandBot {

    private final JDA jda;

    public AddCommandBot(final JDA jda){
        this.jda = jda;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // recogemos el objeto que modifica los comandos
        //CommandListUpdateAction updateCommands = jda.updateCommands();
        // ToDo eliminar modificacion de comandos para pruebas
        Guild guild = jda.getGuilds().getFirst();
        System.out.println(guild.getName());
        CommandListUpdateAction updateCommands = guild.updateCommands();

        updateCommands.addCommands(getCommands()).queue();
    }

    public ArrayList<SlashCommandData> getCommands() {
        ArrayList<SlashCommandData> commandList = new ArrayList<>();
        for (CommandConstants command: CommandConstants.values()) {
            SlashCommandData commandData = Commands.slash(command.getName(), command.getDescription());
            commandData = commandData.setContexts(InteractionContextType.GUILD);
            commandData = commandData.setDefaultPermissions(command.getDefaultMemberPermissions());
            if (command.getOptions() != null) {
                for (CommandConstants.Option option : command.getOptions()) {
                    commandData.addOption(
                            option.getType(), option.getName(), option.getDescription(),
                            option.isRequired(), option.isAutocomplete()
                    );
                }
            }
            commandList.add(commandData);
        }

        return commandList;
    }
}
