package io.lemonjuice.flandre_bot;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Log4j2
public class ConfigInit {
    public static void initConfig() {
        File configFolder = new File("./config");
        if(!configFolder.exists()) {
            configFolder.mkdirs();
        }
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] configs = resolver.getResources("classpath:export/config/*");
            for (Resource cfg : configs) {
                File cfgFile = new File(configFolder.getPath() + "/" + cfg.getFilename());
                if(!cfgFile.exists()) {
                    releaseConfig(cfg.getFilename());
                }
            }
        } catch (IOException e) {
            log.error("初始化配置文件失败！", e);
        }
    }

    private static void releaseConfig(String fileName) throws IOException {
        try (InputStream input = Start.class.getClassLoader().getResourceAsStream("export/config/" + fileName);
             FileOutputStream output = new FileOutputStream("./config/" + fileName)) {
            output.write(input.readAllBytes());
        }
    }
}
