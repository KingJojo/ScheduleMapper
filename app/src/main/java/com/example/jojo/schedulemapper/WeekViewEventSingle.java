package com.example.jojo.schedulemapper;

/**
 * Created by kevinkuo on 11/5/15.
 */
import com.alamkanak.weekview.WeekViewEvent;

import java.lang.String;

public class WeekViewEventSingle extends WeekViewEvent{

    private String mNote;

    public WeekViewEventSingle(long id, String name, int startYear, int startMonth, int startDay,
                               int startHour, int startMinute, int endYear, int endMonth,
                               int endDay, int endHour, int endMinute, String location) {

        super(id, name, startYear, startMonth, startDay, startHour, startMinute, endYear,
                endMonth, endDay, endHour, endMinute);
        super.setLocation(location);

    }

}
