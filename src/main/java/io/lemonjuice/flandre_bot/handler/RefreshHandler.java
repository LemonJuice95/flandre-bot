package io.lemonjuice.flandre_bot.handler;

import io.lemonjuice.flandre_bot.command.group.interest.GroupFortuneCookieCommand;
import io.lemonjuice.flandre_bot.network.SQLCore;
import io.lemonjuice.flandre_bot.network.WSClientCore;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@EnableScheduling
@EnableAsync
@Component
@Log4j2
public class RefreshHandler {

    @Getter
    private static final RefreshHandler instance = new RefreshHandler();

    @Scheduled(cron = "0 0 0 * * ?")
    @Async
    public void dailyRefresh() {
        refreshSignIn();
        log.info("每日刷新已完成！");
    }

    @Scheduled(cron = "0 0 */6 * * ?")
    @Async
    public void refresh6Hours() {
        refreshMessages();
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Async
    public void refreshPerHour() {
        NicknameManager.save();
        GroupFortuneCookieCommand.refresh();
    }

    private static void refreshSignIn() {
        try (Connection co = SQLCore.getInstance().startConnection();
             Statement st = co.createStatement()) {
            st.execute("TRUNCATE TABLE sign_in;");
        } catch (SQLException e) {
            log.error(e);
        }
    }

    private static void refreshMessages() {
        JSONObject json = new JSONObject();
        json.put("action", "_mark_all_as_read");
        WSClientCore.getInstance().sendText(json.toString());
        log.info("已将所有消息设为已读");
    }
}