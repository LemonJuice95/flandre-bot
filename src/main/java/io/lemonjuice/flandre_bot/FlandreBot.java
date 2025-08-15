package io.lemonjuice.flandre_bot;

import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.nio.file.FileSystems;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class FlandreBot {
    private static final boolean isDevMode = false;
    @Getter
    private static final String jarPath = FileSystems.getDefault().getPath("").toAbsolutePath() + (isDevMode ? "/run/" :  "/");
    @Getter
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    @Getter
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(FlandreBot.class, args);
    }
}
