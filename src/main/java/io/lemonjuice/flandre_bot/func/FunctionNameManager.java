package io.lemonjuice.flandre_bot.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionNameManager {

    public static final Map<String, String> CONFIGURABLE_GROUP_FUNCTIONS = Map.ofEntries(
            Map.entry("骰娘", "dice"),
            Map.entry("随机东方图", "random_touhou_image"),
            Map.entry("舞萌查询", "maimai_query"),
            Map.entry("舞萌小游戏", "maimai_games")
    );
    public static final List<String> GROUP_FUNCTIONS = List.of(
            "dice",
            "random_touhou_image",
            "maimai_query",
            "maimai_games",
            "k11_number",
            "mai_deepseek"
    );
}
