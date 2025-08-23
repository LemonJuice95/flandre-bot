package io.lemonjuice.flandre_bot.resources;

import io.lemonjuice.flandre_bot.command.group.interaction.GroupHelloCommand;
import io.lemonjuice.flandre_bot.command.group.interaction.GroupPingCommand;
import io.lemonjuice.flandre_bot.utils.PeriodOfDay;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
public class InteractRepliesLoader extends ResourceLoader {
    @Override
    public void load() {
        this.loadPingReplies();
        this.loadHelloReplies();
    }

    private void loadPingReplies() {
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("assets/interact/ping.txt");
             InputStreamReader reader = new InputStreamReader(input);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            GroupPingCommand.REPLIES = bufferedReader.lines().toList();
        } catch (IOException e) {
            log.error("加载ping回复文本异常！");
            log.error(e);
        }
    }


    private void loadHelloReplies() {
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("assets/interact/hello.json")) {
            JSONTokener jsonTokener = new JSONTokener(input);
            JSONObject json = new JSONObject(jsonTokener);

            for(PeriodOfDay period : PeriodOfDay.values()) {
                String key = period.toString().toLowerCase();
                JSONArray array = json.optJSONArray(key);
                List<String> list = array != null ?
                        StreamSupport.stream(array.spliterator(), false).map(Object::toString).collect(Collectors.toList()) :
                        new ArrayList<>();
                GroupHelloCommand.REPLIES.put(period, list);
            }
        } catch (IOException e) {
            log.error("加载“你好”回复文本异常！");
            log.error(e);
        }
    }
}
