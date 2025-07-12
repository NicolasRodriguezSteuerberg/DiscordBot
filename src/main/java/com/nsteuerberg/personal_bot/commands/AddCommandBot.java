package com.nsteuerberg.personal_bot.commands;

import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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
        // recorremos cada comando
        for(CommandConstants command: CommandConstants.values()){
            SlashCommandData commandData = Commands.slash(command.getName(), command.getDescription());
            // solo permitimos que se puedan usar en un server
            commandData = commandData.setContexts(InteractionContextType.GUILD);
            commandData = commandData.setDefaultPermissions(command.getDefaultMemberPermissions());
            // agregamos las opciones de ser el caso de tener
            if (command.getOptions()!= null) {
                for (CommandConstants.Option option : command.getOptions()) {
                    commandData.addOption(
                            option.getType(), option.getName(), option.getDescription(),
                            option.isRequired(), option.isAutocomplete()
                    );
                }
            }
            updateCommands = updateCommands.addCommands(commandData);
        }

        updateCommands.queue();
    }
}
