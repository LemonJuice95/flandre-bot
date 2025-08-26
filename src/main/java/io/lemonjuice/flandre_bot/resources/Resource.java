package io.lemonjuice.flandre_bot.resources;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

@Log4j2
public abstract class Resource<T> implements Supplier<T> {
    private final String resPath;
    private final T dummyValue;

    private volatile T res;

    public Resource(String resPath, T dummyValue) {
        this.resPath = resPath;
        this.dummyValue = dummyValue;
    }

    public T get() {
        if (this.res == null) {
            synchronized (this) {
                if (this.res == null) {
                    this.init();
                }
            }
        }
        return this.res;
    }

    public synchronized void init() {
        log.info("正在加载资源: {}", this.resPath);
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(this.resPath)) {
            this.res = this.load(input);
        } catch (Exception e) {
            log.error("加载内部资源失败: {}", this.resPath, e);
            this.res = this.dummyValue;
        }
    }

    protected abstract T load(InputStream input) throws IOException;
}