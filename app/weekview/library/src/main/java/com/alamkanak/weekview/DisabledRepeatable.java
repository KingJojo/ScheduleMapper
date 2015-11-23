package com.alamkanak.weekview;

import android.graphics.Color;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nathan on 11/15/15.
 */

@ParseClassName("DisabledRepeatable")
public class DisabledRepeatable extends ParseObject{
    private Calendar mStartTime, mEndTime;
    private long mRepeatableId = -1;

    // not used, included for Parse
    public DisabledRepeatable() {

    }

    public DisabledRepeatable(long repeatableId, Calendar startTime, Calendar endTime) {

        mRepeatableId = repeatableId;
        put("repeatableId", mRepeatableId);

        this.mStartTime = startTime;
        put("startTime", mStartTime.getTime());

        this.mEndTime = endTime;
        put("endTime", mEndTime.getTime());
    }

    public Calendar getStartTime() {
        if(mStartTime == null)
            mStartTime = dateToCal(getDate("startTime"));
        return mStartTime;
    }

    public void setStartTime(Calendar startTime) {
        this.mStartTime = startTime;
        put("startTime", mStartTime.getTime());
    }

    public Calendar getEndTime() {
        if(mEndTime == null)
            mEndTime = dateToCal(getDate("endTime"));
        return mEndTime;
    }

    public void setEndTime(Calendar endTime) {
        this.mEndTime = endTime;
        put("endTime", mEndTime.getTime());
    }

    public void setRepeatableId(long repeatableId) {

        this.mRepeatableId = repeatableId;
        put("repeatableId", mRepeatableId);

    }

    public long getRepeatableId() {

        if(mRepeatableId == -1)
            mRepeatableId = getLong("repeatableId");

        return mRepeatableId;
    }

    public static Calendar dateToCal(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
