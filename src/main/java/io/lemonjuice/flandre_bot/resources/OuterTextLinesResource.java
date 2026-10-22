package io.lemonjuice.flandre_bot.resources;

import io.lemonjuice.flandre_bot_framework.resource.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class OuterTextLinesResource extends Resource<List<String>> {
    public OuterTextLinesResource(String path) {
        super(path, List.of(), Source.FILE_SYSTEM);
    }

    @Override
    protected List<String> load(InputStream input) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            return bufferedReader.lines().toList();
        }
    }
}
