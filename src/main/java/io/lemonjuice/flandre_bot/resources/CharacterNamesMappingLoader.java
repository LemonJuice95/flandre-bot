package io.lemonjuice.flandre_bot.resources;

import io.lemonjuice.flandre_bot.command.group.func.RandomTouhouImageCommand;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;

public class CharacterNamesMappingLoader extends ResourceLoader {
    @Override
    public void load() {
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("assets/touhou_character_names_map.json")) {
            JSONTokener jsonTokener = new JSONTokener(input);
            RandomTouhouImageCommand.NAME_TAGS = new JSONObject(jsonTokener);
        } catch (IOException e) {
            log.error("加载东方角色名映射表失败! ");
            log.error(e);
        }
    }
}
