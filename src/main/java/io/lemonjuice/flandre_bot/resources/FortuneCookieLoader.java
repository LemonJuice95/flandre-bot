package io.lemonjuice.flandre_bot.resources;

import io.lemonjuice.flandre_bot.command.group.interest.GroupFortuneCookieCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FortuneCookieLoader extends ResourceLoader {
    @Override
    public void load() {
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("assets/fortune_cookies.txt");
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            GroupFortuneCookieCommand.COOKIES = bufferedReader.lines().toList();
        } catch (IOException e) {
            log.error("加载幸运饼干文本失败! ");
            log.error(e);
        }
    }
}
