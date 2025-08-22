package io.lemonjuice.flandre_bot.command.group.debug;

import io.lemonjuice.flandre_bot.handler.RefreshHandler;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

public class ManualDailyRefreshCommand extends GroupDebugCommandRunner {
    public ManualDailyRefreshCommand(Message command) {
        super(command);
    }

    @Override
    public boolean validate() {
        String message = this.command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(this.command.selfId) + "/手动每日刷新");
    }

    @Override
    public void apply() {
        RefreshHandler.getInstance().dailyRefresh();
        RefreshHandler.getInstance().refresh6Hours();
        RefreshHandler.getInstance().refreshPerHour();
        SendingUtils.sendGroupText(this.command.groupId, "[Debug]已手动完成刷新");
    }
}
