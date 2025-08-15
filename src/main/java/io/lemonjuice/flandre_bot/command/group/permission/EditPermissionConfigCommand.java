package io.lemonjuice.flandre_bot.command.group.permission;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;

public class EditPermissionConfigCommand extends GroupCommandRunner {
    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.ADMIN;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.startsWith(CQCodeUtils.at(command.selfId) + "/编辑权限");
    }

    @Override
    public void apply(Message command) {
    }
}
