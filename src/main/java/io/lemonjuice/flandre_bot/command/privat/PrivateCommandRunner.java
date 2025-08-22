package io.lemonjuice.flandre_bot.command.privat;

import io.lemonjuice.flandre_bot.message.Message;

public abstract class PrivateCommandRunner {
    protected final Message command;

    public PrivateCommandRunner(Message command) {
        this.command = command;
    }

    public boolean needsFriend() {
        return false;
    }

    public boolean isDebugCommand() {
        return false;
    }

    public abstract boolean validate();
    public abstract void apply();
}
