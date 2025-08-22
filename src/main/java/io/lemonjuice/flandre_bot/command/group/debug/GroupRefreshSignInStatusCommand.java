package io.lemonjuice.flandre_bot.command.group.debug;

import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.network.SQLCore;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log4j2
public class GroupRefreshSignInStatusCommand extends GroupDebugCommandRunner {
    public GroupRefreshSignInStatusCommand(Message command) {
        super(command);
    }

    @Override
    public boolean validate() {
        String message = this.command.message.replace(" ", "");
        return message.equals(CQCodeUtils.at(this.command.selfId) + "/刷新签到状态");
    }

    @Override
    public void apply() {
        try (Connection co = SQLCore.getInstance().startConnection();
             PreparedStatement ps = co.prepareStatement("DELETE FROM sign_in WHERE group_id=?")) {
            ps.setLong(1, this.command.groupId);
            ps.execute();
            SendingUtils.sendGroupText(this.command.groupId, "[Debug]已清除本群本日的签到记录");
        } catch (SQLException e) {
            log.error(e);
        }
    }
}
