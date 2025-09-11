package io.lemonjuice.flandre_bot.scheduled;

import io.lemonjuice.flan_sql_support.network.SQLCore;
import io.lemonjuice.flandre_bot.commands.CommandRunningStatistics;
import lombok.extern.log4j.Log4j2;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Log4j2
public class DailyRefreshJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.refreshSignIn();
        CommandRunningStatistics.dailyClear();
    }

    private void refreshSignIn() {
        try (Connection co = SQLCore.getInstance().startConnection();
             Statement st = co.createStatement()) {
            st.execute("TRUNCATE TABLE sign_in;");
        } catch (SQLException e) {
            log.error(e);
        }
    }
}
