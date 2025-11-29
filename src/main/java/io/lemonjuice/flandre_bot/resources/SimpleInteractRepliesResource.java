package io.lemonjuice.flandre_bot.resources;

import io.lemonjuice.flandre_bot_framework.resource.Resource;
import io.lemonjuice.flandre_bot_framework.utils.CQCode;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

@Log4j2
public class SimpleInteractRepliesResource extends Resource<Map<String, List<String>>> {
    private static final Pattern image_pattern = Pattern.compile("<(local|net)Image:(\\S+)>");

    public SimpleInteractRepliesResource() {
        super("assets/interact/simple.json", Collections.unmodifiableMap(new HashMap<>()));
    }

    @Override
    protected Map<String, List<String>> load(InputStream input) throws IOException {
        JSONTokener tokener = new JSONTokener(input);
        JSONObject json = new JSONObject(tokener);
        Map<String, List<String>> result = new HashMap<>();
        for (String s : json.keySet()) {
            try {
                JSONObject action = json.getJSONObject(s);
                JSONArray msgJson = action.getJSONArray("msg");
                JSONArray repliesJson = action.getJSONArray("replies");
                List<String> replies = StreamSupport.stream(repliesJson.spliterator(), false)
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

    private String parseImage(String str) {
        Matcher matcher = image_pattern.matcher(str);
        while (matcher.find()) {
            String type = matcher.group(1);
            String path = matcher.group(2);
            if(type.equals("local")) {
                File imageFile = new File(path);
                if (imageFile.exists()) {
                    str = matcher.replaceFirst(CQCode.image("file:///" + imageFile.getAbsolutePath()));
                } else {
                    log.warn("找不到简单回复文本中的本地图片: {}", path);
                    str = matcher.replaceFirst("");
                }
            } else {
                str = matcher.replaceFirst(CQCode.image(path));
            }
            matcher = image_pattern.matcher(str);
        }
        return str;
    }
}