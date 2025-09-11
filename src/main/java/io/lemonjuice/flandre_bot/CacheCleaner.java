package io.lemonjuice.flandre_bot;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class CacheCleaner {
    private static final List<Path> RESERVE_WHITELIST = new ArrayList<>();

    private static final String SONG_DATA = "./cache/mai_song_data/";

    static {
        addWhiteList(SONG_DATA);
    }

    private static final String PATH = "./cache/";

    private static void addWhiteList(String path) {
        RESERVE_WHITELIST.add(Path.of(path).toAbsolutePath().normalize());
    }

    public static void clean() {
        log.info("正在清理缓存...");
        try {
            Files.walkFileTree(Path.of(PATH).toAbsolutePath().normalize(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if(RESERVE_WHITELIST.contains(dir))
                        return FileVisitResult.SKIP_SUBTREE;
                    else
                        return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if(!RESERVE_WHITELIST.contains(file))
                        Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }


                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("清理缓存失败！", e);
        }
    }
}