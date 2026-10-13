package io.lemonjuice.flandre_bot.chat_bot.commands;

import io.lemonjuice.flandre_bot.chat_bot.ChatBotHandler;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.command.group.GroupPatternCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.RegexNode;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PromptInjectCommand extends GroupPatternCommandRunner {
    private static final Pattern pattern = Pattern.compile("/prompt_inject\\s+(write|overwrite|clear)\\s+(.+)?");
    public static final MessagePattern commandPattern = MessagePattern.builder()
            .nextNode(AtNode.atBot())
            .startGroup()
            .nextNode(new RegexNode(pattern))
            .endGroup()
            .build();

    public PromptInjectCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.DEBUG;
    }

    @Override
    public MessagePattern getPattern() {
        return commandPattern;
    }

    @Override
    public void apply() {
        MessageSegmentList captured = this.matcher.group(1);
        if(captured != null && captured.getFirst() instanceof TextMessageSegment textSeg) {
            Matcher matcher1 = pattern.matcher(textSeg.getContent().trim());
            if(matcher1.matches()) {
                String operation = matcher1.group(1);
                String content = matcher1.group(2);
                switch (operation) {
                    case "write" ->  ChatBotHandler.injectPrompt(this.command.groupId,
                            ChatBotHandler.INJECTED_PROMPTS.getOrDefault(this.command.groupId, ChatBotHandler.SYS_MSG) + "\n" + content);
                    case "overwrite" -> ChatBotHandler.injectPrompt(this.command.groupId, content);
                    case "clear" -> ChatBotHandler.clearInject(this.command.groupId);
                }
                this.command.getContext().replyWithText("操作成功~");
                return;
            }
        }
        this.command.getContext().replyWithText("命令格式似乎有误呢……检查一下吧~");
    }
}
