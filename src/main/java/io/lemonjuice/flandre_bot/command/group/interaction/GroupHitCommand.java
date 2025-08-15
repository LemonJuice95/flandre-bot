package io.lemonjuice.flandre_bot.command.group.interaction;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

public class GroupHitCommand extends GroupCommandRunner {
    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(command.selfId) + "打你") ||
               message.equals(CQCodeUtils.at(command.selfId) + "打芙兰") ||
               message.equals("打芙兰");
    }

    @Override
    public void apply(Message command) {
        SendingUtils.sendGroupText(command.groupId, "不许打芙兰！（捏拳）");
    }
}
