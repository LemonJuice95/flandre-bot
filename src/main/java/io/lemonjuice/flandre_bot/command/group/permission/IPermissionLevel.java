package io.lemonjuice.flandre_bot.command.group.permission;

import io.lemonjuice.flandre_bot.message.Message;

public interface IPermissionLevel {
    public boolean validatePermission(Message command);
}
