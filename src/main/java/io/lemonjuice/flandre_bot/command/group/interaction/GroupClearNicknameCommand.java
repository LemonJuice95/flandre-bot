package io.lemonjuice.flandre_bot.command.group.interaction;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.NickNameManager;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

public class GroupClearNicknameCommand extends GroupCommandRunner {
    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(command.selfId) + "/清除称呼");
    }

    @Override
    public void apply(Message command) {
        NickNameManager.removeNickname(command.userId);
        SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "了解~ 芙兰以后不会再用昵称称呼你啦");
    }
}
