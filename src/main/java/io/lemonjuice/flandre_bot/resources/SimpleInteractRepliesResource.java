package io.lemonjuice.flandre_bot.resources;

import io.lemonjuice.flandre_bot_framework.message.segment.ImageMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import io.lemonjuice.flandre_bot_framework.resource.Resource;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

@Log4j2
public class SimpleInteractRepliesResource extends Resource<Map<String, List<List<MessageSegment>>>> {
    private static final Pattern image_pattern = Pattern.compile("<(local|net)Image:(\\S+)>");

    public SimpleInteractRepliesResource() {
        super("assets/interact/simple.json", Collections.unmodifiableMap(new HashMap<>()));
    }

    @Override
    protected Map<String, List<List<MessageSegment>>> load(InputStream input) throws IOException {
        JSONTokener tokener = new JSONTokener(input);
        JSONObject json = new JSONObject(tokener);
        Map<String, List<List<MessageSegment>>> result = new HashMap<>();
        for (String s : json.keySet()) {
            try {
                JSONObject action = json.getJSONObject(s);
                JSONArray msgJson = action.getJSONArray("msg");
                JSONArray repliesJson = action.getJSONArray("replies");
                List<List<MessageSegment>> replies = StreamSupport.stream(repliesJson.spliterator(), false)
                        .map(String::valueOf)
                        .map(this::parseImage)
                        .toList();
                StreamSupport.stream(msgJson.spliterator(), false)
                        .map(String::valueOf)
                        .forEach(msg -> {
                            result.put(msg, replies);
                        });
            } catch (JSONException e) {
                log.warn("加载简单互动回复文本时出现问题，请检查assets/interact/simple.json内的\"{}\"块", s);
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private List<MessageSegment> parseImage(String str) {
        Matcher matcher = image_pattern.matcher(str);
        List<MessageSegment> result = new ArrayList<>();
        while (matcher.find()) {
            String type = matcher.group(1);
            String path = matcher.group(2);
            if(type.equals("local")) {
                File imageFile = new File(path);
                if(imageFile.exists()) {
                    result.add(new TextMessageSegment(str.substring(0, matcher.start())));
                    result.add(new ImageMessageSegment(imageFile));
                } else {
                    log.warn("找不到简单回复文本中的本地图片: {}", path);
                }
            } else {
                result.add(new TextMessageSegment(str.substring(0, matcher.start())));
                result.add(new ImageMessageSegment(path));
            }
            str = matcher.replaceFirst("");
            matcher = image_pattern.matcher(str);
        }
        if(!str.isBlank()) {
            result.add(new TextMessageSegment(str));
        }
        return result;
    }
}