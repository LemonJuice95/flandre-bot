package io.lemonjuice.flandre_bot.commands.group.interaction;

import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
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
            .nextNode(new RegexNode(commandPattern))
            .startGroup()
            .nextNode(new AnySegmentNode())
            .endGroup(MessagePattern.GroupFlag.LOOP, MessagePattern.GroupFlag.OPTIONAL)
            .build();

    public GroupNicknameCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        return messagePattern.matcher(this.command.message.trim()).matches();
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
        String message = this.command.message
                .getSegments()
                .get(1)
                .toString()
                .trim();
        Matcher matcher = commandPattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }
}