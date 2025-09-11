package io.lemonjuice.flandre_bot;

import com.google.common.eventbus.Subscribe;
import io.lemonjuice.flandre_bot.commands.CommandInit;
import io.lemonjuice.flandre_bot.commands.group.special.K11PeopleNumberCommand;
import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot.scheduled.ScheduledTaskManager;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.meta.BotInitEvent;
import io.lemonjuice.flandre_bot_framework.event.meta.BotStopEvent;

@EventSubscriber
public class FlandreBotInit {
    @Subscribe
    public void initBot(BotInitEvent event) {
        ResourceInit.RESOURCES.load();
        CommandInit.COMMANDS.load();
        NicknameManager.init();
        ScheduledTaskManager.init();
        K11PeopleNumberCommand.init();
    }

    @Subscribe
    public void onBotStop(BotStopEvent event) {
        K11PeopleNumberCommand.save();
        NicknameManager.save();
        CacheCleaner.clean();
    }
}
