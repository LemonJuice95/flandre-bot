package io.lemonjuice.flandre_bot.command.group.permission;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;

public class EditPermissionConfigCommand extends GroupCommandRunner {
    public EditPermissionConfigCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.ADMIN;
    }

    @Override
    public boolean validate() {
        String message = this.command.message.replaceAll("", "");
        return message.startsWith(CQCodeUtils.at(this.command.selfId) + "/编辑权限");
    }

    @Override
    public void apply() {
    }
}
