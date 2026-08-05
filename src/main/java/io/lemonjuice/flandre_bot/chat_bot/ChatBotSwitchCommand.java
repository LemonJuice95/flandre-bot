package io.lemonjuice.flandre_bot.chat_bot;

import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.RegexNode;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatBotSwitchCommand extends GroupCommandRunner {
    private static final Pattern pattern = Pattern.compile("/(开启|关闭)ai聊天", Pattern.CASE_INSENSITIVE);
    public static final MessagePattern commandPattern = MessagePattern.builder()
            .nextNode(AtNode.atBot())
            .nextNode(new RegexNode(pattern))
            .build();

    public ChatBotSwitchCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        return commandPattern.matcher(this.command).matches();
    }

    @Override
    public void apply() {
        String text = this.command.message.get(1).toString();
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()) {
            String operation = matcher.group(1);
            if(operation.equals("开启")) {
                ChatBotHandler.enableChatBot(this.command.groupId);
            } else {
                ChatBotHandler.disableChatBot(this.command.groupId);
            }
            this.command.getContext().replyWithText("ai聊天已" +
                    operation +
                    (operation.equals("开启") ? "顺便一提：关闭ai聊天会丢失所有上下文哦~" : ""));
        }
    }
}
