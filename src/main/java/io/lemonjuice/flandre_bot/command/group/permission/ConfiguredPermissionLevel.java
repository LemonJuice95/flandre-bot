package io.lemonjuice.flandre_bot.command.group.permission;

import io.lemonjuice.flandre_bot.message.Message;

public class ConfiguredPermissionLevel implements IPermissionLevel {
    private final IPermissionLevel defaultValue;
    private final String permissionName;

    public ConfiguredPermissionLevel(IPermissionLevel defaultValue, String permissionName) {
        this.defaultValue = defaultValue;
        this.permissionName = permissionName;
    }

    //TODO 接入SQL获取对应权限
    @Override
    public synchronized boolean validatePermission(Message command) {
        return this.defaultValue.validatePermission(command);
    }
}
