package io.lemonjuice.flandre_bot.commands.group.interaction;

import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot.utils.PeriodOfDay;
import io.lemonjuice.flandre_bot_framework.command.group.SimpleGroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.MessageToSend;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.util.Arrays;
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
        String[] replyRaw = repliesNow.get(random.nextInt(repliesNow.size()))
                .split("<user>", -1);
        MessageToSend reply = this.command.getContext().prepareMessageToSend();
        for(int i = 0; i < replyRaw.length - 1; i++) {
            reply.appendText(replyRaw[i]);
            NicknameManager.appendNickname(this.command.userId, reply);
        }
        reply.appendText(replyRaw[replyRaw.length - 1]);
        reply.send();
    }
}