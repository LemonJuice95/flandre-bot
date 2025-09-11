package io.lemonjuice.flandre_bot.func;

import com.google.common.eventbus.Subscribe;
import io.lemonjuice.flandre_bot_framework.command.CommandRunner;
import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.msg.CommandRunEvent;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.utils.CQCode;

@EventSubscriber
public class FunctionCheckProcessor {
    @Subscribe
    public void onCommandRun(CommandRunEvent.Pre event) {

    }
}
