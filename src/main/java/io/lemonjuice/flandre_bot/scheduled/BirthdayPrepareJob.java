package io.lemonjuice.flandre_bot.scheduled;

import io.lemonjuice.flandre_bot.utils.BirthdayManager;
import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.message.GroupContext;
import io.lemonjuice.flandre_bot_framework.message.MessageToSend;
import io.lemonjuice.flandre_bot_framework.model.GroupMember;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BirthdayPrepareJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LocalDate date = LocalDate.now();
        date = date.plusDays(1);

        List<Long> celebrants = BirthdayManager.getCelebrantWithDate(date.getMonthValue(), date.getDayOfMonth());
        Map<Long, List<Long>> preparedCelebrants = new HashMap<>();
        ContextManager.getGroups().forEach(ctx -> preparedCelebrants.put(ctx.getGroupId(), new ArrayList<>()));

        for(long userId : celebrants) {
            ContextManager.getGroups()
                    .stream()
                    .filter(ctx -> {
                        for(GroupMember member : ctx.getMembers()) {
                            if(member.userId == userId) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .forEach(ctx -> {
                        if(preparedCelebrants.containsKey(ctx.getGroupId())) {
                            preparedCelebrants.get(ctx.getGroupId()).add(userId);
                        }
                    });
        }
        preparedCelebrants.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        List<MessageToSend> messages = new ArrayList<>();
        preparedCelebrants.forEach((groupId, users) -> {
            MessageToSend preparedMsg = ContextManager.getGroup(groupId).prepareMessageToSend();
            for(long userId : users) {
                preparedMsg.appendAt(userId);
                preparedMsg.appendText(" ");
            }
            preparedMsg.appendText(String.format("芙兰祝%s生日快乐哦~", users.size() == 1 ? "你" : "你们"));
            messages.add(preparedMsg);
        });

        SendBirthdayGreetingsJob.PREPARED_MESSAGES.addAll(messages);
    }
}
