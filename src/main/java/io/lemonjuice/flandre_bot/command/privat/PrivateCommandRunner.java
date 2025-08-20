package io.lemonjuice.flandre_bot.command.privat;

import io.lemonjuice.flandre_bot.message.Message;

public abstract class PrivateCommandRunner implements Cloneable {
    public boolean needsFriend() {
        return false;
    }

    public boolean isDebugCommand() {
        return false;
    }

    public abstract boolean validate(Message command);
    public abstract void apply(Message command);

    @Override
    public PrivateCommandRunner clone() {
        try {
            return (PrivateCommandRunner) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
