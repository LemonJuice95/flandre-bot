package io.lemonjuice.flandre_bot.scheduled;

import io.lemonjuice.flandre_bot.commands.group.interest.GroupFortuneCookieCommand;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class PerHourRefreshJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        NicknameManager.save();
        GroupFortuneCookieCommand.refresh();
    }
}
