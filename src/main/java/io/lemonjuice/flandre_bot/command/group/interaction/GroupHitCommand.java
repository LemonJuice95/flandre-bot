package io.lemonjuice.flandre_bot.command.group.interaction;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

public class GroupHitCommand extends GroupCommandRunner {
    public GroupHitCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        String message = this.command.message.replace(" ", "");
        return message.equals(CQCodeUtils.at(this.command.selfId) + "打你") ||
               message.equals(CQCodeUtils.at(this.command.selfId) + "打芙兰") ||
               message.equals("打芙兰");
    }

    @Override
    public void apply() {
        SendingUtils.sendGroupText(this.command.groupId, "不许打芙兰！（捏拳）");
    }
}
