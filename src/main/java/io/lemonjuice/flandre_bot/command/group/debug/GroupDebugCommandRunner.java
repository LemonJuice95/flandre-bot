package io.lemonjuice.flandre_bot.command.group.debug;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;

public abstract class GroupDebugCommandRunner extends GroupCommandRunner {
    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.DEBUG;
    }
}
