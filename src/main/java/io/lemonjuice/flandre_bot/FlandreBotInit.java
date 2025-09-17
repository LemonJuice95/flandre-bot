package io.lemonjuice.flandre_bot;

import com.google.common.eventbus.Subscribe;
import io.lemonjuice.flan_sql_support.event.SQLPreCloseEvent;
import io.lemonjuice.flan_sql_support.network.SQLCore;
import io.lemonjuice.flandre_bot.commands.CommandInit;
import io.lemonjuice.flandre_bot.commands.group.special.K11PeopleNumberCommand;
import io.lemonjuice.flandre_bot.console.ConsoleCommandInit;
import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot.scheduled.ScheduledTaskManager;
import io.lemonjuice.flandre_bot.test.TestEvent;
import io.lemonjuice.flandre_bot.test.TestEvent2;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.meta.BotInitEvent;
import io.lemonjuice.flandre_bot_framework.event.meta.BotStopEvent;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.CompletableFuture;

@EventSubscriber
@Log4j2
public class FlandreBotInit {
    @Subscribe
    public void initBot(BotInitEvent event) {
        ConsoleCommandInit.CONSOLE_COMMANDS.load();
        ResourceInit.RESOURCES.load();
        CommandInit.COMMANDS.load();
        NicknameManager.init();
        ScheduledTaskManager.init();
        K11PeopleNumberCommand.init();
    }

    @Subscribe
    public void onBotStop(BotStopEvent event) {
        CacheCleaner.clean();
    }

    @Subscribe
    public void preSqlClose(SQLPreCloseEvent event) {
        K11PeopleNumberCommand.save();
        NicknameManager.save();

    }
}
