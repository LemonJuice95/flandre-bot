package io.lemonjuice.flandre_bot;

import io.lemonjuice.flan_sql_support.event.SQLPreCloseEvent;
import io.lemonjuice.flandre_bot.commands.CommandInit;
import io.lemonjuice.flandre_bot.commands.group.special.K11PeopleNumberCommand;
import io.lemonjuice.flandre_bot.console.ConsoleCommandInit;
import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot.scheduled.ScheduledTaskManager;
import io.lemonjuice.flandre_bot.segment.FileMessageSegment;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import io.lemonjuice.flandre_bot_framework.event.meta.BotInitEvent;
import io.lemonjuice.flandre_bot_framework.event.meta.BotStopEvent;
import io.lemonjuice.flandre_bot_framework.event.meta.SegmentTypeRegisterEvent;
import lombok.extern.log4j.Log4j2;

@EventSubscriber
@Log4j2
public class FlandreBotInit {
    @SubscribeEvent
    public void initBot(BotInitEvent event) {
        ConsoleCommandInit.CONSOLE_COMMANDS.load();
        ResourceInit.RESOURCES.load();
        CommandInit.COMMANDS.load();
        NicknameManager.init();
        ScheduledTaskManager.init();
        K11PeopleNumberCommand.init();
    }

    @SubscribeEvent
    public void onSegmentRegister(SegmentTypeRegisterEvent event) {
        event.register("file", FileMessageSegment::new);
    }

    @SubscribeEvent
    public void onBotStop(BotStopEvent event) {
        CacheCleaner.clean();
    }

    @SubscribeEvent
    public void preSqlClose(SQLPreCloseEvent event) {
        K11PeopleNumberCommand.save();
        NicknameManager.save();
    }
}
