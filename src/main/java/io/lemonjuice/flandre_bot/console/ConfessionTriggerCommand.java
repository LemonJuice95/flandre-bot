package io.lemonjuice.flandre_bot.console;

import io.lemonjuice.flandre_bot.chat_bot.ChatBotHandler;
import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRunner;

import java.util.List;

public class ConfessionTriggerCommand extends ConsoleCommandRunner {
    public ConfessionTriggerCommand(String[] args) {
        super(args);
    }

    @Override
    public void apply() {
        try {
            String operation = this.args[0];
            switch (operation) {
                case "on" -> {
                    ChatBotHandler.CONFESSION_SWITCH = true;
                    BotConsole.println("是时候了。");
                }
                case "off" -> {
                    ChatBotHandler.CONFESSION_SWITCH = false;
                    BotConsole.println("在害怕什么呢？");
                }
                default -> BotConsole.println("格式错误，正确格式: confession <on|off>");
            }
        } catch (Exception e) {
            BotConsole.println("格式错误，正确格式: confession <on|off>");
        }
    }

    @Override
    public List<String> getCommandBodies() {
        return List.of("confession");
    }

    @Override
    public String getUsingFormat() {
        return "confession <on|off>";
    }

    @Override
    public String getDescription() {
        return "仅可意会";
    }
}
