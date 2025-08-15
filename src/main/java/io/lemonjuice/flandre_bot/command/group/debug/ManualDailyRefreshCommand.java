package io.lemonjuice.flandre_bot.command.group.debug;

import io.lemonjuice.flandre_bot.handler.RefreshHandler;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

public class ManualDailyRefreshCommand extends GroupDebugCommandRunner {
    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(command.selfId) + "/手动每日刷新");
    }

    @Override
    public void apply(Message command) {
        RefreshHandler.getInstance().dailyRefresh();
        RefreshHandler.getInstance().refresh6Hours();
        RefreshHandler.getInstance().refreshPerHour();
        SendingUtils.sendGroupText(command.groupId, "[Debug]已手动完成刷新");
    }
}
