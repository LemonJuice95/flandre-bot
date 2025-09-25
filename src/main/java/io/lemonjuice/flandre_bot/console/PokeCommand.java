package io.lemonjuice.flandre_bot.console;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.GroupContext;
import io.lemonjuice.flandre_bot_framework.message.IMessageContext;
import io.lemonjuice.flandre_bot_framework.message.MessageContext;

import java.util.List;

public class PokeCommand extends ConsoleCommandRunner {
    public PokeCommand(String[] args) {
        super(args);
    }

    @Override
    public void apply() {
        try {
            String type = this.args[0];
            IMessageContext context;
            switch (type) {
                case "g", "group" -> {
                    long groupId = Long.parseLong(this.args[1]);
                    long userId = Long.parseLong(this.args[2]);
                    context = new GroupContext(groupId).withUserId(userId);
                }
                case "p", "private" -> {
                    long userId = Long.parseLong(this.args[1]);
                    context = ContextManager.getFriend(userId);
                }
                default -> throw new IllegalArgumentException("未知类型");
            }
            context.poke();
            BotConsole.println("戳一戳已发送");
        } catch (Exception e) {
            BotConsole.println("格式错误，命令用法：poke <g(roup)|p(rivate)> [群号] <qq号>");
        }
    }

    @Override
    public List<String> getCommandBodies() {
        return List.of("poke");
    }

    @Override
    public String getUsingFormat() {
        return "'poke <g(roup)|p(rivate)> [群号] <qq号>'";
    }

    @Override
    public String getDescription() {
        return "发送戳一戳";
    }
}
