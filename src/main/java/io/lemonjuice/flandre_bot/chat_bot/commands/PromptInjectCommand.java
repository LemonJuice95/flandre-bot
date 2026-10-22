package io.lemonjuice.flandre_bot.chat_bot.commands;

import io.lemonjuice.flandre_bot.chat_bot.ChatBotHandler;
import io.lemonjuice.flandre_bot.resources.ResourceInit;
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
    private static final Pattern pattern = Pattern.compile("/prompt_inject\\s+(write|overwrite|clear|preset|show_presets)(\\s+.+)?");
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
                if(!operation.equals("clear") && content == null) {
                    this.getCommand().getContext().replyWithText("命令格式似乎有误呢……检查一下吧~");
                    return;
                }
                switch (operation) {
                    case "write" ->  ChatBotHandler.injectPrompt(this.getCommand().groupId,
                            ChatBotHandler.INJECTED_PROMPTS.getOrDefault(this.getCommand().groupId, ChatBotHandler.SYS_MSG) + "\n" + content.trim());
                    case "overwrite" -> ChatBotHandler.injectPrompt(this.getCommand().groupId, content.trim());
                    case "clear" -> ChatBotHandler.clearInject(this.getCommand().groupId);
                    case "preset" -> {
                        if(!ChatBotHandler.injectPromptWithPreset(this.getCommand().groupId, content.trim())) {
                            this.getCommand().getContext().replyWithText("没有这样的预设哦~");
                            return;
                        }
                    }
                    case "show_presets" -> {
                        String reply = "可用预设列表：\ndefault\n" + String.join("\n", ResourceInit.CB_PROMPT_PRESET_BOM.get());
                        this.getCommand().getContext().replyWithText(reply);
                        return;
                    }
                }
                this.getCommand().getContext().replyWithText("操作成功~");
                return;
            }
        }
        this.getCommand().getContext().replyWithText("命令格式似乎有误呢……检查一下吧~");
    }
}
