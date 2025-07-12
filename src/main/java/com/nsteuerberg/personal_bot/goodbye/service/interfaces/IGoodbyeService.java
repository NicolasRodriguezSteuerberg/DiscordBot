package com.nsteuerberg.personal_bot.goodbye.service.interfaces;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

public interface IGoodbyeService {
    void onMemberLeft(GuildMemberRemoveEvent event);
}
