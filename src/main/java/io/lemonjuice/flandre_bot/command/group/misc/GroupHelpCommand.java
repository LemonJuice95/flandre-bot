package io.lemonjuice.flandre_bot.command.group.misc;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.resources.ResourceRegister;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupHelpCommand extends GroupCommandRunner {
    private static final List<String> DOC = ResourceRegister.HELP_DOC_MAIN.get();

    public GroupHelpCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        String message = this.command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(this.command.selfId) + "/help") ||
                message.equals(CQCodeUtils.at(this.command.selfId) + "/帮助");
    }

    @Override
    public void apply() {
        SendingUtils.sendGroupForwardText(this.command.selfId, this.command.groupId, DOC);
    }
}
