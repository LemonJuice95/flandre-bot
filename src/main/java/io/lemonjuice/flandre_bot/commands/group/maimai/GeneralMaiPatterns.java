package io.lemonjuice.flandre_bot.commands.group.maimai;

import io.lemonjuice.flan_mai_plugin.games.condition.LevelCondition;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class GeneralMaiPatterns {
    public static final Map<String, LevelCondition.Mode> LEVEL_MODE_MAP = new HashMap<>();
    public static final Pattern levelCondition = Pattern.compile("等级([><]?=?)(\\d+\\+?\\??)");

    static {
        LEVEL_MODE_MAP.put("<", LevelCondition.Mode.LESS);
        LEVEL_MODE_MAP.put("<=", LevelCondition.Mode.LESS_OR_EQUALS);
        LEVEL_MODE_MAP.put("=", LevelCondition.Mode.EQUALS);
        LEVEL_MODE_MAP.put(">=", LevelCondition.Mode.GREATER_OR_EQUALS);
        LEVEL_MODE_MAP.put(">", LevelCondition.Mode.GREATER);
    }
}
