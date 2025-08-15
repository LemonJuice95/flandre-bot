package io.lemonjuice.flandre_bot.command.group.misc;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupHelpCommand extends GroupCommandRunner {
    public static List<String> DOC = new ArrayList<>();

    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(command.selfId) + "/help") ||
                message.equals(CQCodeUtils.at(command.selfId) + "/帮助");
    }

    @Override
    public void apply(Message command) {
        SendingUtils.sendGroupForwardText(command.selfId, command.groupId, DOC);
    }
}
