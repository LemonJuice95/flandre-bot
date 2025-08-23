package io.lemonjuice.flandre_bot.command.group.interaction;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import io.lemonjuice.flandre_bot.utils.PeriodOfDay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GroupHelloCommand extends GroupCommandRunner {
    public static Map<PeriodOfDay, List<String>> REPLIES = new HashMap<>();

    public GroupHelloCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        String message = this.command.message.replace(" ", "");
        return message.equals(CQCodeUtils.at(this.command.selfId) + "你好");
    }

    @Override
    public void apply() {
        Random random = ThreadLocalRandom.current();
        List<String> repliesNow = REPLIES.get(PeriodOfDay.getCurrent());
        String reply = repliesNow.get(random.nextInt(repliesNow.size()))
                                 .replace("<user>", NicknameManager.getNickname(this.command.userId));
        SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + reply);
    }
}
