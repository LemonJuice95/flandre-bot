package io.lemonjuice.flandre_bot.commands.group.interaction;

import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessageMatcher;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AnySegmentNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.RegexNode;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupNicknameCommand extends GroupCommandRunner {
    private static final Pattern commandPattern = Pattern.compile("/称呼\\s+(\\S*)");
    private static final MessagePattern messagePattern = new MessagePattern.Builder()
            .nextNode(AtNode.atBot())
            .startGroup()
            .nextNode(new RegexNode(commandPattern))
            .endGroup()
            .build();

    private final MessageMatcher matcher;

    public GroupNicknameCommand(Message command) {
        super(command);
        this.matcher = messagePattern.matcher(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        return messagePattern.matcher(this.command.message.trim()).simplyMatches();
    }

    @Override
    public void apply() {
        String nickname = this.getNickname();
        if(!nickname.isEmpty()) {
            NicknameManager.updateNickname(this.command.userId, nickname);
            this.command.getContext().replyWithText("唔……记住啦！\n芙兰以后就叫你" + nickname + "啦！");
        } else {
            this.command.getContext().replyWithText("诶？要芙兰叫你什么？没有听清呢……");
        }
    }

    private String getNickname() {
        this.matcher.reset();
        if(this.matcher.matches()) {
            String message = this.matcher.group(1).toString();
            Matcher matcher = commandPattern.matcher(message);
            return matcher.find() ? matcher.group(1) : "";
        }
        return "";
    }
}