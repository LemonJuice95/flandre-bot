package io.lemonjuice.flandre_bot.console;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRunner;
import io.lemonjuice.flandre_bot_framework.console.ConsoleListener;
import io.lemonjuice.flandre_bot_framework.message.FriendContext;
import io.lemonjuice.flandre_bot_framework.message.GroupContext;
import io.lemonjuice.flandre_bot_framework.message.MessageContext;

import java.util.List;

public class SendTextCommand extends ConsoleCommandRunner {
    public SendTextCommand(String[] args) {
        super(args);
    }

    @Override
    public void apply() {
        try {
            String type = this.args[0];
            if(!this.args[1].equals("all")) {
                long id = Long.parseLong(this.args[1]);
                MessageContext context;
                switch (type) {
                    case "group" -> context = new GroupContext(id);
                    case "private" -> context = new FriendContext(id);
                    default -> context = new MessageContext();
                }
                context.sendText(this.args[2]);
            } else {
                switch (type) {
                    case "group" -> ContextManager.getGroups().forEach(ctx -> ctx.sendText(this.args[2]));
                    case "private" -> ContextManager.getFriends().forEach(ctx -> ctx.sendText(this.args[2]));
                    default -> {
                        BotConsole.println("格式错误，命令用法: sendmsg <group|private> <qqId|all> <消息内容>");
                        return;
                    }
                }
            }

            BotConsole.println("消息已发送");
        } catch (Exception e) {
            BotConsole.println("格式错误，命令用法: sendmsg <group|private> <qqId|all> <消息内容>");
        }
    }

    @Override
    public List<String> getCommandBodies() {
        return List.of("sendmsg");
    }

    @Override
    public String getDescription() {
        return "向qq发送消息";
    }

    @Override
    public String getUsingFormat() {
        return "'sendmsg <group|private> <qqId|all> <消息内容>'";
    }
}
