package io.lemonjuice.flandre_bot.resources;

import io.lemonjuice.flandre_bot.command.group.daily.GroupSignInCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SignInResourcesLoader extends ResourceLoader {
    @Override
    public void load() {
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("assets/sign_in/fortunes.txt");
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            GroupSignInCommand.FORTUNES = bufferedReader.lines().toList();
        } catch (IOException e) {
            log.error("加载运势失败!");
            log.error(e);
        }
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("assets/sign_in/things.txt");
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            GroupSignInCommand.THINGS = bufferedReader.lines().toList();
        } catch (IOException e) {
            log.error("加载事项失败!");
            log.error(e);
        }
    }
}
