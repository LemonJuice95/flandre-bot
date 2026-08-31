package io.lemonjuice.flandre_bot.scheduled;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.message.MessageToSend;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SendBirthdayGreetingsJob implements Job {
    static final ConcurrentHashMap<Long, List<Long>> PREPARED_CELEBRANTS = new ConcurrentHashMap<>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        PREPARED_CELEBRANTS.forEach((groupId, users) -> {
            if(users.isEmpty()) {
                return;
            }
            MessageToSend message = ContextManager.getGroup(groupId).prepareMessageToSend();
            users.forEach(u -> message.appendAt(u).appendText(" "));
            message.appendText(String.format("芙兰祝%s生日快乐哦~", users.size() == 1 ? "你" : "你们"));
            message.send();
        });
        PREPARED_CELEBRANTS.clear();
    }
}
