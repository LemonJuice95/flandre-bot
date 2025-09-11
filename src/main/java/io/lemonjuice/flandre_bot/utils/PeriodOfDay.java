package io.lemonjuice.flandre_bot.utils;

import java.time.LocalTime;

public enum PeriodOfDay {
    DAWN(4, 6),
    MORNING(6, 9),
    FORENOON(9, 11),
    NOON(11, 13),
    AFTERNOON(13, 17),
    EVENING(17, 19),
    NIGHT(19, 23),
    LATE_NIGHT(23, 4);

    private final int startsAt; //包含这个整点
    private final int endsAt; //不包含这个整点

    private PeriodOfDay(int startsAt, int endsAt) {
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public static PeriodOfDay getCurrent() {
        LocalTime time = LocalTime.now();
        int hour = time.getHour();
        for(int i = 0; i < PeriodOfDay.values().length - 1; i++) {
            if(hour >= PeriodOfDay.values()[i].startsAt && hour < PeriodOfDay.values()[i].endsAt) {
                return PeriodOfDay.values()[i];
            }
        }
        return LATE_NIGHT;
    }
}
