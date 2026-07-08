package io.lemonjuice.flandre_bot.config;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Supplier;

@Log4j2
public class FlandreBotConfig {
    private static final Properties properties = new Properties();
    private static final File cfgFile = new File("./config/bot.properties");

    public static final Supplier<String> DEEPSEEK_API_KEY = () -> properties.getProperty("deepseek.api_key");

    public static void init() {
        try (InputStream input = new FileInputStream(cfgFile)) {
            properties.load(input);
        } catch (IOException e) {
            log.warn("自定义配置读取失败！", e);
        }
    }
}
