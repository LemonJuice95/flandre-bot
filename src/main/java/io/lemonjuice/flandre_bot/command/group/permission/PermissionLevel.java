package io.lemonjuice.flandre_bot.command.group.permission;

import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.DebugModeToolkit;

public enum PermissionLevel implements IPermissionLevel{
    NORMAL {
        @Override
        public boolean validatePermission(Message command) {
            return true;
        }
    },
    ADMIN {
        @Override
        public boolean validatePermission(Message command) {
            return command.sender.role.equals("admin") ||
                    OWNER.validatePermission(command);
        }
    },
    OWNER {
        @Override
        public boolean validatePermission(Message command) {
            return command.sender.role.equals("owner") ||
                    DEBUG.validatePermission(command);
        }
    },
    DEBUG {
        @Override
        public boolean validatePermission(Message command) {
            return DebugModeToolkit.hasDebugPermission(command.userId);
        }
    }
}
