package io.lemonjuice.flandre_bot.resources;

import io.lemonjuice.flandre_bot_framework.resource.Resource;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Log4j2
public class SimpleInteractRepliesResource extends Resource<Map<String, List<String>>> {

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
}