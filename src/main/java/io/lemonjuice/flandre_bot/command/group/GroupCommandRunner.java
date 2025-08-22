package io.lemonjuice.flandre_bot.command.group;

import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;

public abstract class GroupCommandRunner {
    protected final Message command;

    public GroupCommandRunner(Message command) {
        this.command = command;
    }

    public abstract IPermissionLevel getPermissionLevel();
    public abstract boolean validate();
    public abstract void apply();
}
