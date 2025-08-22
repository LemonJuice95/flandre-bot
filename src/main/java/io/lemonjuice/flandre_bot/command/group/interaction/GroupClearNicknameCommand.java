package io.lemonjuice.flandre_bot.command.group.interaction;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

public class GroupClearNicknameCommand extends GroupCommandRunner {
    public GroupClearNicknameCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        String message = this.command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(this.command.selfId) + "/清除称呼");
    }

    @Override
    public void apply() {
        NicknameManager.removeNickname(this.command.userId);
        SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "了解~ 芙兰以后不会再用昵称称呼你啦");
    }
}
