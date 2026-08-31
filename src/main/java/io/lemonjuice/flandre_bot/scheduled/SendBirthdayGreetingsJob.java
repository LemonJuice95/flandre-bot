package io.lemonjuice.flandre_bot.scheduled;

import io.lemonjuice.flandre_bot_framework.message.MessageToSend;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.concurrent.CopyOnWriteArrayList;

public class SendBirthdayGreetingsJob implements Job {
    static final CopyOnWriteArrayList<MessageToSend> PREPARED_MESSAGES = new CopyOnWriteArrayList<>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        PREPARED_MESSAGES.forEach(MessageToSend::send);
        PREPARED_MESSAGES.clear();
    }
}
