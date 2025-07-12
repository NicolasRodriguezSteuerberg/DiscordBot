package com.nsteuerberg.personal_bot.welcome.service.interfaces;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public interface IWelcomeChatService {
    void onGuildJoin(GuildJoinEvent event);
    void onMemberJoin(GuildMemberJoinEvent event);

    /*
    void changeMemberCount();
    void changeDateEntry();
    void changeTitleMessage();
    void changeRuleChat();
    void addMessage();
    void removeMessage();
    void updateMessage();
     */
}
