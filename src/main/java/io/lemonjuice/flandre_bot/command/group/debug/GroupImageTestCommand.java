package io.lemonjuice.flandre_bot.command.group.debug;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.reference.LocalImages;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

public class GroupImageTestCommand extends GroupCommandRunner {
    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.DEBUG;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(command.selfId) + "/图片发送测试");
    }

    @Override
    public void apply(Message command) {
        SendingUtils.sendGroupText(command.groupId, CQCodeUtils.localExpressionImage(LocalImages.FLAN_LOVE));
    }

}
