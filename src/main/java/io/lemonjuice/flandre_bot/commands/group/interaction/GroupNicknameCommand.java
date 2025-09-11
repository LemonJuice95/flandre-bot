package io.lemonjuice.flandre_bot.commands.group.interaction;

import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import io.lemonjuice.flandre_bot_framework.utils.CQCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupNicknameCommand extends GroupCommandRunner {
    private static final Pattern cqPattern = Pattern.compile("\\[CQ:\\S+]");
    private static final Pattern commandPattern = Pattern.compile("/称呼\\s+(\\S+)");

    public GroupNicknameCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        String message = this.command.message.replace(CQCode.at(this.command.selfId), "").trim();
        return this.command.message.startsWith(CQCode.at(this.command.selfId)) &&
                message.startsWith("/称呼");
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
        if(cqPattern.matcher(this.command.message).results().count() > 1) {
            return "";
        }
        String message = this.command.message
                .replace(CQCode.at(this.command.selfId), "")
                .trim();
        Matcher matcher = commandPattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }
}