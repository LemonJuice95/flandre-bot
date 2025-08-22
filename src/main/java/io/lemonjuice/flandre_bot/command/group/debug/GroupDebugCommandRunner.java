package io.lemonjuice.flandre_bot.command.group.debug;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;

public abstract class GroupDebugCommandRunner extends GroupCommandRunner {
    public GroupDebugCommandRunner(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.DEBUG;
    }
}
