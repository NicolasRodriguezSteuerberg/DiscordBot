package com.nsteuerberg.personal_bot.utils.constants;

import lombok.Getter;

@Getter
public enum ButtonIds {
    GOODBYE_PREV_MSG("prev_msg_goodbye:"),
    GOODBYE_NEXT_MSG("next_msg_goodbye:"),
    GOODBYE_EDIT_MSG("edit_msg_goodbye:"),
    GOODBYE_DEL_MSG("del_msg_goodbye:"),
    TIER_LIST_TEXT_NEXT("next_tier_list_text:"),
    TIER_LIST_TEXT_PREV("prev_tier_list_text:"),
    TIER_LIST_VOICE_NEXT("next_tier_list_voice:"),
    TIER_LIST_VOICE_PREV("prev_tier_list_voice:")
    ;

    private final String baseId;

     ButtonIds(String baseId) {
        this.baseId = baseId;
    }

}
