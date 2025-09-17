package io.lemonjuice.flandre_bot;

import com.google.common.eventbus.Subscribe;
import io.lemonjuice.flan_sql_support.event.SQLPreCloseEvent;
import io.lemonjuice.flandre_bot.commands.group.special.K11PeopleNumberCommand;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;

@EventSubscriber
public class TestSQL {
    @Subscribe
    public void preSqlClose(SQLPreCloseEvent event) {
        K11PeopleNumberCommand.save();
        NicknameManager.save();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {

        }
    }
}
