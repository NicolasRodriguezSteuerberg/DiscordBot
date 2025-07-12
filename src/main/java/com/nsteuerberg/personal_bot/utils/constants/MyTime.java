package com.nsteuerberg.personal_bot.utils.constants;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class MyTime {
    public static ZonedDateTime getNow() {
        return ZonedDateTime.now(ZoneId.of("Europe/Madrid"));
    }
}
