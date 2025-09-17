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

        new Thread(() -> {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {

            }
            BotEventBus.post(new TestEvent());
            log.info("Test 1");
        }, "Test").start();
    }

    @Subscribe
    public void onBotStop(BotStopEvent event) {
        CacheCleaner.clean();
    }

    @Subscribe
    public void onTest(TestEvent event) {
        log.info("msg 1, Time={}", System.currentTimeMillis());
        BotEventBus.post(new TestEvent2());
        log.info("msg 3, Time={}", System.currentTimeMillis());
    }

    @Subscribe
    public void onTest2(TestEvent2 event) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {

        }
        log.info("msg 2, Time={}", System.currentTimeMillis());
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {

        }
    }

    @Subscribe
    public void preSqlClose(SQLPreCloseEvent event) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {

        }
        K11PeopleNumberCommand.save();
        NicknameManager.save();

    }
}
