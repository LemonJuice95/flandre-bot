package io.lemonjuice.flandre_bot.reference;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.Properties;

@Log4j2
public class NetworkRefs {
    private static final String PATH = "config/network.properties";

    public static String WS_URL = "";
    public static String WS_TOKEN = "";

    public static String SQL_URL = "";
    public static String SQL_USERNAME = "";
    public static String SQL_PASSWORD = "";

    static {
        File cfg = new File(PATH);
        if(!cfg.getParentFile().exists()) {
            cfg.getParentFile().mkdir();
        }
        if(!cfg.exists()) {
            releaseFile();
        }
        load();
    }

    private static void releaseFile() {
        try (InputStream input = NetworkRefs.class.getClassLoader().getResourceAsStream("release/config/network.properties");
             FileOutputStream output = new FileOutputStream(PATH)) {
            output.write(input.readAllBytes());
        } catch (Exception e) {
            log.error("释放网络相关配置文件失败！", e);
        }
    }

    private static void load() {
        try (FileInputStream input = new FileInputStream(PATH)) {
            Properties properties = new Properties();
            properties.load(input);

            WS_URL = properties.getProperty("ws.url");
            WS_TOKEN = properties.getProperty("ws.token");

            SQL_URL = properties.getProperty("sql.url");
            SQL_USERNAME = properties.getProperty("sql.username");
            SQL_PASSWORD = properties.getProperty("sql.password");
        } catch (Exception e) {
            log.error("加载网络相关配置失败！", e);
        }
    }
}
