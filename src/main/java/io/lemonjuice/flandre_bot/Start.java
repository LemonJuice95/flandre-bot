package io.lemonjuice.flandre_bot;

import io.lemonjuice.flandre_bot.network.SQLCore;
import io.lemonjuice.flandre_bot.network.WSClientCore;
import io.lemonjuice.flandre_bot.network.WSReconnect;
import io.lemonjuice.flandre_bot.handler.ResourceLoadHandler;
import io.lemonjuice.flandre_bot.reference.NetworkRefs;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Log4j2
public class Start implements CommandLineRunner {

    @Override
    public void run(String... args) {
        initConfig();
        ResourceLoadHandler.getInstance().loadResources();
        WSClientCore.startWatchDog();
        if(!WSClientCore.connect(NetworkRefs.WS_URL, NetworkRefs.WS_TOKEN)) {
            Thread.startVirtualThread(new WSReconnect());
        }
        SQLCore.connect(NetworkRefs.SQL_URL, NetworkRefs.SQL_USERNAME, NetworkRefs.SQL_PASSWORD);
        NicknameManager.init();
    }

    private static void initConfig() {
        File configFolder = new File("./config");
        if(!configFolder.exists()) {
            configFolder.mkdir();
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
