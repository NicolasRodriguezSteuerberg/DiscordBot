package com.nsteuerberg.personal_bot.configuration;

import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomName {
    CommandConstants value();
}
