package io.lemonjuice.flandre_bot.resources;

import io.lemonjuice.flandre_bot.utils.PeriodOfDay;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.resource.*;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class ResourceInit {
    public static final ResourceRegister RESOURCES = new ResourceRegister();

    //简单互动回复
    public static final Resource<Map<String, List<List<MessageSegment>>>> SIMPLE_INTERACTION_REPLIES = RESOURCES.register(new SimpleInteractRepliesResource());

    //帮助文档
    public static final Resource<List<List<MessageSegment>>> HELP_DOC_MAIN = RESOURCES.register(new HelpDocResource("assets/help_doc/help_doc.txt"));
    public static final Resource<List<List<MessageSegment>>> HELP_DOC_MAI = RESOURCES.register(new HelpDocResource("assets/help_doc/maimai.txt"));

    //签到
    public static final Resource<List<String>> SIGN_IN_FORTUNES = RESOURCES.register(new TextLinesResource("assets/sign_in/fortunes.txt"));
    public static final Resource<List<String>> SIGN_IN_THINGS = RESOURCES.register(new TextLinesResource("assets/sign_in/things.txt"));

    //幸运曲奇
    public static final Resource<List<String>> FORTUNE_COOKIES = RESOURCES.register(new TextLinesResource("assets/fortune_cookies.txt"));

    //其他互动功能
    public static final Resource<Map<PeriodOfDay, List<String>>> HELLO_REPLIES = RESOURCES.register(new HelloRepliesResource());

    //东方角色名
    public static final Resource<JSONObject> TOUHOU_CHARACTER_NAMES = RESOURCES.register(new JsonResource("assets/touhou_character_names_map.json"));

}
