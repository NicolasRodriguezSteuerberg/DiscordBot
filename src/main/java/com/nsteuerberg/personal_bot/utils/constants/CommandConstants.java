package com.nsteuerberg.personal_bot.utils.constants;

import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Getter
public enum CommandConstants {
    SET_WELCOME_OPTIONS(
            "set_welcome_options",
            "Conjunto de cambios para el mensaje de bienvenida",
            DefaultMemberPermissions.DISABLED,
            new Option[]{
                    new Option("welcome_chat", "Chat de texto donde mandar los mensajes de bienvenida", OptionType.CHANNEL, false, false),
                    new Option("title", "Titulo que se va a mostrar a la hora de mandar el mensaje de bienvenida", OptionType.STRING, false, false),
                    new Option("add_date", "Si agregar o no la fecha de entrada a la hora de mandar el mensaje de bienvenida", OptionType.BOOLEAN, false, false),
                    new Option("add_member_count", "Si agregar el conteo de miembros a la hora de mandar el mensaje de bienvenida", OptionType.BOOLEAN, false, false),
                    new Option("rule_chat", "Canal de reglas", OptionType.CHANNEL, false, false)
            }
    ),
    SEE_WELCOME_MESSAGES(
            "see_welcome_messages",
            "Muestra la lista de los mensajes de bienvenida",
            DefaultMemberPermissions.DISABLED,
            null
    ),
    ADD_WELCOME_MESSAGE(
            "add_welcome_message",
            "Agrega un nuevo mensaje de bienvenida",
            DefaultMemberPermissions.DISABLED,
            new Option[] {
                    new Option("message", "Mensaje, escriba '%u' para que se mencione al que acaba de unirse", OptionType.STRING, true,false)
            }
    ),
    SET_GOODBYE_OPTIONS(
            "set_goodbye_options",
            "Conjunto de cambios para el mensaje de despedida",
            DefaultMemberPermissions.DISABLED,
            new Option[]{
                new Option("goodbye_chat", "Chat de texto donde mandar los mensajes de despedida", OptionType.CHANNEL, false, false),
                new Option("title", "Titulo que se va a mostrar a la hora de mandar el mensaje de despedida", OptionType.STRING, false, false),
                new Option("add_date", "Si agregar o no la fecha de entrada a la hora de mandar el mensaje de despedida", OptionType.BOOLEAN, false, false),
                new Option("add_member_count", "Si agregar el conteo de miembros a la hora de mandar el mensaje de despedida", OptionType.BOOLEAN, false, false)
            }
    ),
    ADD_GOODBYE_MESSAGE(
            "add_goodbye_message",
            "Agrega un nuevo mensaje de bienvenida",
            DefaultMemberPermissions.DISABLED,
            new Option[] {
                    new Option("message", "Mensaje, escriba '%u' para que se mencione al que acaba de unirse", OptionType.STRING, true,false)
            }
    ),
    SEE_GOODBYE_MESSAGES(
            "see_goodbye_messages",
            "Saca una paginacion con los mensajes de despedida",
            DefaultMemberPermissions.DISABLED,
            null
    ),
    // SCORE
    TIER_LIST_TEXT(
            "tier_list_text",
            "Top de los más activos en mensajes",
            DefaultMemberPermissions.ENABLED,
            null
    ),
    TIER_LIST_VOICE(
            "tier_list_voice",
            "Top de los mas activos en canales de voz",
            DefaultMemberPermissions.ENABLED,
            null
    ),
    SCORE(
            "score",
            "Puntuación personal (tanto de chat como de voz)",
            DefaultMemberPermissions.ENABLED,
            new Option[]{
                new Option("user", "Nombre de usuario del que obtener la puntuacion", OptionType.USER, false, false)
            }
    ),
    /*
    SET_ROLE_REACTION(
            "set_role_reaction",
            "Agrega un mensaje en el que se tiene que reaccionar con un rol para añadirselo",
            DefaultMemberPermissions.DISABLED,
            new Option[] {
                    new Option("role", "Nombre del rol que se agregara al usuario al poner el rol", OptionType.ROLE, true, false),
                    new Option("emoji", "Emoji con el que hay que reaccionar", OptionType.STRING, true, false),
                    new Option("chat", "Nombre del chat donde mandar el mensaje", OptionType.CHANNEL, false, false)
            }
    ),

    // MUSIC
    PLAY("play", "Reproduce una cancion", DefaultMemberPermissions.DISABLED, new Option[]{
            new Option("song", "Nombre de la cancion a reproducir", OptionType.STRING, true, false)
    }),
    */
    ;

    // Atributos y constructor
    private String name;
    private String description;
    private DefaultMemberPermissions defaultMemberPermissions;
    private Option[] options;

    CommandConstants(String name, String description, DefaultMemberPermissions defaultPermission, Option[] options){
        this.name = name;
        this.description = description;
        this.defaultMemberPermissions = defaultPermission;
        this.options = options;
    }

    @Getter
    public static class Option {
        private final String name;
        private final String description;
        private final OptionType type;
        private final boolean required;
        private final boolean autocomplete;


        public Option(String name, String description, OptionType type, boolean required, boolean autocomplete) {
            this.name = name;
            this.description = description;
            this.type = type;
            this.required = required;
            this.autocomplete = autocomplete;
        }
    }
}
