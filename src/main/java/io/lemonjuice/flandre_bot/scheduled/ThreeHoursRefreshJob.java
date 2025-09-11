package io.lemonjuice.flandre_bot.scheduled;

import io.lemonjuice.flandre_bot.CacheCleaner;
import io.lemonjuice.flandre_bot_framework.network.WSClientCore;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Log4j2
public class ThreeHoursRefreshJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.refreshMessages();
        CacheCleaner.clean();
    }

    private void refreshMessages() {
        JSONObject json = new JSONObject();
        json.put("action", "_mark_all_as_read");
        WSClientCore.getInstance().sendText(json.toString());
        log.info("已将所有消息设为已读");
    }
}
