package io.lemonjuice.flandre_bot.resources;

import io.lemonjuice.flandre_bot.utils.PeriodOfDay;
import io.lemonjuice.flandre_bot_framework.resource.Resource;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
public class HelloRepliesResource extends Resource<Map<PeriodOfDay, List<String>>> {
    private static final String PATH = "assets/interact/hello.json";

    public HelloRepliesResource() {
        super(PATH, Collections.unmodifiableMap(new HashMap<>()));
    }

    @Override
    protected Map<PeriodOfDay, List<String>> load(InputStream input) {
        Map<PeriodOfDay, List<String>> result = new HashMap<>();
        JSONTokener jsonTokener = new JSONTokener(input);
        JSONObject json = new JSONObject(jsonTokener);

        for (PeriodOfDay period : PeriodOfDay.values()) {
            String key = period.toString().toLowerCase();
            JSONArray array = json.optJSONArray(key);
            List<String> list = array != null ?
                    StreamSupport.stream(array.spliterator(), false).map(Object::toString).collect(Collectors.toList()) :
                    new ArrayList<>();
            result.put(period, list);
        }

        return result;
    }
}
