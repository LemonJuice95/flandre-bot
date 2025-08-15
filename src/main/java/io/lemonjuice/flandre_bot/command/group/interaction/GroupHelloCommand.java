package io.lemonjuice.flandre_bot.command.group.interaction;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.NickNameManager;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import io.lemonjuice.flandre_bot.utils.TimeNames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GroupHelloCommand extends GroupCommandRunner {
    public static Map<TimeNames, List<String>> REPLIES = new HashMap<>();

    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(command.selfId) + "你好");
    }

    @Override
    public void apply(Message command) {
        Random random = ThreadLocalRandom.current();
        List<String> repliesNow = REPLIES.get(TimeNames.getCurrent());
        String reply = repliesNow.get(random.nextInt(repliesNow.size()))
                                 .replace("<user>", NickNameManager.getNickname(command.userId));
        SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + reply);
    }
}
