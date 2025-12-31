package io.lemonjuice.flandre_bot.console;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRunner;

import java.util.List;

public class TempCommand extends ConsoleCommandRunner {
    public TempCommand(String[] args) {
        super(args);
    }

    @Override
    public List<String> getCommandBodies() {
        return List.of("temp");
    }

    @Override
    public void apply() {
        ContextManager.getGroups().forEach(ctx -> {
            ctx.sendText("米娜桑新年快乐哦~");
        });
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getUsingFormat() {
        return "";
    }
}
