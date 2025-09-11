package io.lemonjuice.flandre_bot.commands.group.interaction;

import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot.utils.PeriodOfDay;
import io.lemonjuice.flandre_bot_framework.command.group.SimpleGroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GroupHelloCommand extends SimpleGroupCommandRunner {
    private static final Map<PeriodOfDay, List<String>> REPLIES = ResourceInit.HELLO_REPLIES.get();

    public GroupHelloCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    protected boolean needAtFirst() {
        return true;
    }

    @Override
    protected String getCommandBody() {
        return "你好";
    }

    @Override
    public void apply() {
        Random random = ThreadLocalRandom.current();
        List<String> repliesNow = REPLIES.get(PeriodOfDay.getCurrent());
        String reply = repliesNow.get(random.nextInt(repliesNow.size()))
                                 .replace("<user>", NicknameManager.getNickname(this.command.userId));
        this.command.getContext().replyWithText(reply);
    }
}