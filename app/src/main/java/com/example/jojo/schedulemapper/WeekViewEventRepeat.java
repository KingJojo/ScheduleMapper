package com.example.jojo.schedulemapper;

/**
 * Created by kevinkuo on 11/5/15.
 */
import com.alamkanak.weekview.WeekViewEvent;

import java.lang.String;
import java.util.Calendar;

public class WeekViewEventRepeat extends WeekViewEvent{

    public WeekViewEventRepeat(long id, String name, String location, Calendar startTime,
                         Calendar endTime, String note) {
        super(id, name, location, startTime, endTime);
        super.setNote(note);

    }

}
