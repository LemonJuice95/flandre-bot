package io.lemonjuice.flandre_bot.command.group.permission;

import java.util.HashMap;
import java.util.Map;

public class PermissionNames {
    public static final Map<String, IPermissionLevel> PERMISSION_LEVELS = new HashMap<>() {{
        put("开放", PermissionLevel.NORMAL);
        put("管理员", PermissionLevel.ADMIN);
        put("仅群主", PermissionLevel.OWNER);
    }};
    public static final Map<String, String> NAME_TABLE = new HashMap<>();

    static {


        register("编辑机厅", "edit_site");
    }

    public static void register(String chatName, String dataName) {
        NAME_TABLE.put(chatName, dataName);
    }
}
