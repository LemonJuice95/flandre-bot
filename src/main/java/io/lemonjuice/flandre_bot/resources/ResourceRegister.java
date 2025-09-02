package io.lemonjuice.flandre_bot.resources;

import io.lemonjuice.flandre_bot.utils.PeriodOfDay;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResourceRegister {
    private static final List<Resource<?>> RESOURCES = new ArrayList<>();

    //帮助文档
    public static final Resource<List<String>> HELP_DOC_MAIN = register(new HelpDocResource("assets/help_doc/help_doc.txt"));
    public static final Resource<List<String>> HELP_DOC_MAI = register(new HelpDocResource("assets/help_doc/maimai.txt"));

    //签到
    public static final Resource<List<String>> SIGN_IN_FORTUNES = register(new TextLinesResource("assets/sign_in/fortunes.txt"));
    public static final Resource<List<String>> SIGN_IN_THINGS = register(new TextLinesResource("assets/sign_in/things.txt"));

    //幸运曲奇
    public static final Resource<List<String>> FORTUNE_COOKIES = register(new TextLinesResource("assets/fortune_cookies.txt"));

    //互动功能
    public static final Resource<Map<String, List<String>>> SIMPLE_INTERACTION_REPLIES = register(new SimpleInteractRepliesLoader());
    public static final Resource<Map<PeriodOfDay, List<String>>> HELLO_REPLIES = register(new HelloRepliesResource());

    //角色名映射表
    public static final Resource<JSONObject> TOUHOU_CHARACTER_NAMES = register(new JsonResource("assets/touhou_character_names_map.json"));

    private static <T> Resource<T> register(Resource<T> res) {
        RESOURCES.add(res);
        return res;
    }

    public static void init() {
        RESOURCES.forEach(Resource::init);
    }
}
