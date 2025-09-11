package io.lemonjuice.flandre_bot.commands.group.interaction;

import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.command.group.SimpleGroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

public class GroupClearNicknameCommand extends SimpleGroupCommandRunner {
    public GroupClearNicknameCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    protected boolean needAtFirst() {
        return true;
    }

    @Override
    protected String getCommandBody() {
        return "/清除称呼";
    }

    @Override
    public void apply() {
        NicknameManager.removeNickname(this.command.userId);
        this.command.getContext().replyWithText("了解~ 芙兰以后不会再用昵称称呼你啦");
    }
}