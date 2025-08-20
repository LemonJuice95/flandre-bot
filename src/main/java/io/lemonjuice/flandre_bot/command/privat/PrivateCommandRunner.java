package io.lemonjuice.flandre_bot.command.privat;

import io.lemonjuice.flandre_bot.message.Message;

/**
 * 此类的实现应当线程安全
 */
public abstract class PrivateCommandRunner {
    public boolean needsFriend() {
        return false;
    }

    public boolean isDebugCommand() {
        return false;
    }

    public abstract boolean validate(Message command);
    public abstract void apply(Message command);
}
