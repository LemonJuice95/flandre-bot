package io.lemonjuice.flandre_bot.command.group.interaction;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroupPingCommand extends GroupCommandRunner {
    public static List<String> REPLIES = new ArrayList<>();

    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message;
        return message.equals(CQCodeUtils.at(command.selfId)) ||
                message.trim().equals("芙兰") ||
                message.replaceAll(" ", "").equals(CQCodeUtils.at(command.selfId) + "芙兰");
    }

    @Override
    public void apply(Message command) {
        String reply = REPLIES.get(new Random().nextInt(REPLIES.size()));
        SendingUtils.sendGroupText(command.groupId, reply);
    }
}
