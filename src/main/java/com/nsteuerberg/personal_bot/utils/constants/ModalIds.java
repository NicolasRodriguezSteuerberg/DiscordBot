package com.nsteuerberg.personal_bot.utils.constants;

import lombok.Getter;

@Getter
public enum ModalIds {
    GOODBYE_EDIT("edit_msg_goodbye:")
    ;

    private final String baseId;

    ModalIds(String baseId) {
        this.baseId = baseId;
    }

}
