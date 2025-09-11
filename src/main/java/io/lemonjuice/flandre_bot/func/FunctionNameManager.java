package io.lemonjuice.flandre_bot.func;

import java.util.HashMap;
import java.util.Map;

public class FunctionNameManager {

    public static Map<String, String> GROUP_FUNCTIONS = new HashMap<>();

    static {
//        GROUP_FUNCTIONS.put("机厅人数查询", "number_of_people");
        GROUP_FUNCTIONS.put("骰娘", "dice");
        GROUP_FUNCTIONS.put("随机东方图", "random_touhou_image");
        GROUP_FUNCTIONS.put("舞萌查询", "maimai_query");
        GROUP_FUNCTIONS.put("舞萌小游戏", "maimai_games");
    }
}
