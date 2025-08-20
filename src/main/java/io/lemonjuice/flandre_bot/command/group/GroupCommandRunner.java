package io.lemonjuice.flandre_bot.command.group;

import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;

/**
 * 此类的实现应当线程安全
 */
public abstract class GroupCommandRunner {
    public abstract IPermissionLevel getPermissionLevel(Message command);
    public abstract boolean validate(Message command);
    public abstract void apply(Message command);
}
