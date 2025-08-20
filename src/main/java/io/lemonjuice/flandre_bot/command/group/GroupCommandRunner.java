package io.lemonjuice.flandre_bot.command.group;

import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;

public abstract class GroupCommandRunner implements Cloneable {
    public abstract IPermissionLevel getPermissionLevel(Message command);
    public abstract boolean validate(Message command);
    public abstract void apply(Message command);

    @Override
    public GroupCommandRunner clone() {
        try {
            return (GroupCommandRunner) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
