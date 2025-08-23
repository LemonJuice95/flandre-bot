package io.lemonjuice.flandre_bot.command.group.debug;

import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import io.lemonjuice.flandre_bot.utils.PeriodOfDay;

public class GroupCurrentTimeCommand extends GroupDebugCommandRunner {
    public GroupCurrentTimeCommand(Message command) {
        super(command);
    }

    @Override
    public boolean validate() {
        String message = this.command.message.replace(" ", "");
        return message.equals(CQCodeUtils.at(this.command.selfId) + "/当前时间");
    }

    @Override
    public void apply() {
        SendingUtils.sendGroupText(this.command.groupId, PeriodOfDay.getCurrent().toString());
    }
}
