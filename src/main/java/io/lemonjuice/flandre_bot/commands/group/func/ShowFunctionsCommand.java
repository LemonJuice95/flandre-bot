package io.lemonjuice.flandre_bot.commands.group.func;

import io.lemonjuice.flandre_bot.func.FunctionNameManager;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.command.group.SimpleGroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import io.lemonjuice.flandre_bot_framework.utils.CQCode;

import java.util.Set;

public class ShowFunctionsCommand extends SimpleGroupCommandRunner {
    public ShowFunctionsCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.ADMIN;
    }

    @Override
    protected boolean needAtFirst() {
        return true;
    }

    @Override
    protected String getCommandBody() {
        return "/可配置功能列表";
    }

    @Override
    public void apply() {
        Set<String> functions = FunctionNameManager.GROUP_FUNCTIONS.keySet();
        StringBuilder messageRaw = new StringBuilder("当前可手动启用/禁用的功能有：\n");
        for(String s : functions) {
            messageRaw.append(s).append("\n");
        }
        this.command.getContext().replyWithText(messageRaw.toString().trim());
    }
}