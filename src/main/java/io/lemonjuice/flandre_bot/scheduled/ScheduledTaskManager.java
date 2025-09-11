package io.lemonjuice.flandre_bot.scheduled;

import lombok.extern.log4j.Log4j2;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

@Log4j2
public class ScheduledTaskManager {
    private static Scheduler scheduler;

    private static void registerTasks() {
        register(JobBuilder.newJob(DailyRefreshJob.class)
                        .withIdentity("dailyRefresh", "refresh")
                        .build(),
                 TriggerBuilder.newTrigger()
                         .withIdentity("dailyTrigger", "refresh")
                         .startNow()
                         .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?"))
                         .build());

        register(JobBuilder.newJob(PerHourRefreshJob.class)
                        .withIdentity("perHourRefresh", "refresh")
                        .build(),
                TriggerBuilder.newTrigger()
                        .withIdentity("perHourTrigger", "refresh")
                        .startNow()
                        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
                        .build());

        register(JobBuilder.newJob(ThreeHoursRefreshJob.class)
                        .withIdentity("threeHoursRefresh", "refresh")
                        .build(),
                TriggerBuilder.newTrigger()
                        .withIdentity("threeHoursTrigger", "refresh")
                        .startNow()
                        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 */3 * * ?"))
                        .build());
    }

    private static void register(JobDetail job, Trigger trigger) {
        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.error("注册定时任务\"{}\"时发生错误", job.getKey().toString() , e);
        }
    }

    public static void init() {
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
            registerTasks();
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("初始化调度器失败！", e);
        }
    }
}
