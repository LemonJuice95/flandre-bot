package io.lemonjuice.flandre_bot.command.group.debug;

import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import io.lemonjuice.flandre_bot.utils.TimeNames;

public class GroupCurrentTimeCommand extends GroupDebugCommandRunner {
    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(command.selfId) + "/当前时间");
    }

    @Override
    public void apply(Message command) {
        SendingUtils.sendGroupText(command.groupId, TimeNames.getCurrent().toString());
    }
}
