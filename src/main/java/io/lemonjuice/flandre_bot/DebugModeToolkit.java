package io.lemonjuice.flandre_bot;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class DebugModeToolkit {
    private static final String PATH = "config/debug.properties";
    private static boolean DEBUG_MODE;

    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }

    public static boolean hasDebugPermission(long uid) {
        return DEBUG_PERMISSION_IDS.contains(uid) && isDebugMode();
    }

    private static final Set<Long> DEBUG_PERMISSION_IDS = new HashSet<>();

    static {
        File cfg = new File(PATH);
        if(!cfg.getParentFile().exists()) {
            cfg.getParentFile().mkdir();
        }
        if(!cfg.exists()) {
            releaseConfig();
        }
        loadConfig();
    }

    private static void releaseConfig() {
        try (InputStream input = DebugModeToolkit.class.getClassLoader().getResourceAsStream("release/config/debug.properties");
             FileOutputStream output = new FileOutputStream(PATH)) {
            output.write(input.readAllBytes());
        } catch (Exception e) {
            log.error("释放调试配置文件失败！", e);
        }
    }

    private static void loadConfig() {
        try (FileInputStream input = new FileInputStream(PATH)) {
            Properties properties = new Properties();
            properties.load(input);

            DEBUG_MODE = Boolean.parseBoolean(properties.getProperty("debug.debug_mode"));

            String ids = properties.getProperty("debug.users");

            Pattern prepare = Pattern.compile("\\{?[\\d\\s,]+}?");
            Matcher prepareMatcher = prepare.matcher(ids);
            ids = prepareMatcher.find() ? prepareMatcher.group() : "";

            Pattern userId = Pattern.compile("\\d+");
            Matcher idMatcher = userId.matcher(ids);
            while(idMatcher.find()) {
                DEBUG_PERMISSION_IDS.add(Long.parseLong(idMatcher.group()));
            }
        } catch (Exception e) {
            log.error("加载调试配置文件失败！", e);
        }
    }
}
