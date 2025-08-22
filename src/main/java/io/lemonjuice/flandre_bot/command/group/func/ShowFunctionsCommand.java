package io.lemonjuice.flandre_bot.command.group.func;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.func.FunctionNameManager;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.Set;

public class ShowFunctionsCommand extends GroupCommandRunner {
    public ShowFunctionsCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.ADMIN;
    }

    @Override
    public boolean validate() {
        String message = this.command.message.replaceAll(" ", "");
        return message.startsWith(CQCodeUtils.at(this.command.selfId) + "/可配置功能列表");
    }

    @Override
    public void apply() {
        Set<String> functions = FunctionNameManager.GROUP_FUNCTIONS.keySet();
        StringBuilder messageRaw = new StringBuilder("当前可手动启用/禁用的功能有：\n");
        for(String s : functions) {
            messageRaw.append(s).append("\n");
        }
        messageRaw.deleteCharAt(messageRaw.length() - 1);
        SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + messageRaw);
    }
}
