package io.lemonjuice.flandre_bot.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ResourceLoader {
    protected final Logger log = LogManager.getLogger();

    public abstract void load();
}
