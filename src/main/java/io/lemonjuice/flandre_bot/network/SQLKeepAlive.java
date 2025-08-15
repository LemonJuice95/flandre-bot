package io.lemonjuice.flandre_bot.network;

import io.lemonjuice.flandre_bot.FlandreBot;
import io.lemonjuice.flandre_bot.reference.NetworkRefs;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;


@Component
@EnableScheduling
@EnableAsync
@Log4j2
public class SQLKeepAlive {
    private static final AtomicInteger failedCount = new AtomicInteger(0);

    @Scheduled(fixedRate = 30 * 1000)
    @Async
    public void keepAlive() {
        if(SQLCore.getInstance() == null) {
            return;
        }
        try (Connection co = SQLCore.getInstance().startConnection();
             Statement st = co.createStatement()) {
            st.execute("SELECT 1");
            failedCount.set(0);
        } catch (SQLException e) {
            log.warn("SQL连接疑似被关闭，正在重连");
            if(!SQLCore.connect(NetworkRefs.SQL_URL, NetworkRefs.SQL_USERNAME, NetworkRefs.SQL_PASSWORD)) {
                if(failedCount.addAndGet(1) >= 5) {
                    log.fatal("已确认SQL连接无法恢复，即将停止应用");
                    int exitCode = SpringApplication.exit(FlandreBot.getApplicationContext(), () -> 0);
                    System.exit(exitCode);
                }
            }
        }
    }
}
