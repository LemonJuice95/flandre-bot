package io.lemonjuice.flandre_bot.resources;

import io.lemonjuice.flandre_bot.command.group.maimai.GroupMaiHelpCommand;
import io.lemonjuice.flandre_bot.command.group.misc.GroupHelpCommand;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelpDocLoader extends ResourceLoader {
    @Override
    public void load() {
        try {
            GroupHelpCommand.DOC = loadDoc("assets/help_doc/help_doc.txt");
            GroupMaiHelpCommand.DOC = loadDoc("assets/help_doc/maimai.txt");
        } catch (IOException e) {
            log.error("加载帮助文档失败! ");
            log.error(e);
        }
    }

    private List<String> loadDoc(String path) throws IOException {
        Pattern image_pattern = Pattern.compile("^<image:([a-zA-Z0-9_.-]+)>$");

        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(path);
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            List<String> contentsRaw = bufferedReader.lines().toList();
            List<String> contents = new ArrayList<>();
            StringBuilder reading = new StringBuilder();
            for(String c : contentsRaw) {
                if(c.equals("<split>") && !reading.isEmpty()) {
                    contents.add(reading.toString().trim());
                    reading = new StringBuilder();
                } else if(c.startsWith("<image:") && c.endsWith(">")) {
                    Matcher matcher = image_pattern.matcher(c);
                    String imagePath = matcher.find() ? matcher.group(1) : "";
                    reading.append(CQCodeUtils.localImage(imagePath));
                } else {
                    reading.append(c).append("\n");
                }
            }
            if(!reading.toString().trim().isEmpty()) {
                contents.add(reading.toString().trim());
            }
            return contents;
        }
    }
}
