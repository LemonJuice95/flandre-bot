package io.lemonjuice.flandre_bot;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

@Log4j2
public class CacheCleaner {
    private static final String PATH = "./cache/";

    public static void clean() {
        try {
            Files.walkFileTree(Path.of(PATH), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
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
