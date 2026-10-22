package io.lemonjuice.flandre_bot.config;

import io.lemonjuice.flandre_bot_framework.config.BotConfig;
import io.lemonjuice.flandre_bot_framework.config.ConfigItem;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Supplier;

@Log4j2
public class FlandreBotConfig {
    private static final BotConfig CONFIG = new BotConfig("./config/bot-custom.properties", "config/bot-custom.properties");

    public static final ConfigItem<String> DEEPSEEK_API_KEY = CONFIG.register(CONFIG::getString, "deepseek.api_key", "");
    public static final ConfigItem<Integer> CHATBOT_CACHE_SIZE = CONFIG.register(CONFIG::getInt, "chatbot.cache.size", 400);
    public static final ConfigItem<String> CHATBOT_PERSISTENCE_ROOT = CONFIG.register(CONFIG::getString, "chatbot.persistence.root_dir", "./persistence/chat_bot/");

    public static void init() {
        CONFIG.load();
    }
}
