package com.nsteuerberg.personal_bot.configuration;

import com.nsteuerberg.personal_bot.goodbye.listener.GoodbyeListener;
import com.nsteuerberg.personal_bot.music.listener.MusicListener;
import com.nsteuerberg.personal_bot.score.listener.ScoreListener;
import com.nsteuerberg.personal_bot.welcome.listener.WelcomeListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfiguration {
    private final WelcomeListener guildListener;
    private final GoodbyeListener goodbyeListener;
    private final ScoreListener scoreListener;
    private final MusicListener musicListener;

    @Value("${discord.token.bot}")
    private String botToken;

    public BotConfiguration(WelcomeListener guildListener, GoodbyeListener goodbyeListener, ScoreListener scoreListener, MusicListener musicListener) {
        this.guildListener = guildListener;
        this.goodbyeListener = goodbyeListener;
        this.scoreListener = scoreListener;
        this.musicListener = musicListener;
    }

    @Bean
    public JDA jdaBuilder() throws InterruptedException {
        return JDABuilder
                .createDefault(botToken)
                .setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS, "hola"))
                .addEventListeners(guildListener, goodbyeListener, scoreListener, musicListener)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .build()
                .awaitReady();
    }
}
