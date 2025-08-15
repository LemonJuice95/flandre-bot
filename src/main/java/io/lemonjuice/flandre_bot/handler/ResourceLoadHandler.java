package io.lemonjuice.flandre_bot.handler;

import io.lemonjuice.flandre_bot.resources.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ResourceLoadHandler {
    @Getter
    private static final ResourceLoadHandler instance = new ResourceLoadHandler();

    private static final List<ResourceLoader> LOADERS = new ArrayList<>();

    private ResourceLoadHandler() {
    }

    static {
        register(new CharacterNamesMappingLoader());
        register(new HelpDocLoader());
        register(new FortuneCookieLoader());
        register(new SignInResourcesLoader());
        register(new InteractRepliesLoader());
    }

    public void loadResources() {
        log.info("正在加载资源...");
        LOADERS.forEach(ResourceLoader::load);
    }

    private static void register(ResourceLoader loader) {
        LOADERS.add(loader);
    }
}
