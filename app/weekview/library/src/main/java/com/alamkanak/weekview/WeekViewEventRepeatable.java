package com.alamkanak.weekview;

import java.util.Calendar;
import java.util.Date;

import android.graphics.Color;
import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by kevinkuo on 11/15/15.
 */

public class WeekViewEventRepeatable{
    private int mStartHour, mStartMinute;
    private int mEndHour, mEndMinute;
    private String mName;
    private String mLocation;
    private String mNote;
    private int mColor;
    private int mBuildingLocation;
    private int prevColor;
    private String mQuarter;
    private boolean days[]; // days[7]
    boolean enabled = true;


    public WeekViewEventRepeatable(String name, int buildingLocation, String location, String note,
                                   int startHour, int startMinute, int endHour, int endMinute,
                                   boolean days[], String quarter) {

        // Add this when parse works. not sure if #e6e6e6 is right tag
        // prevColor = Color.parseColor("#e6e6e6");

        mName = name;
        mBuildingLocation = buildingLocation;
        mLocation = location;
        mNote = note;
        mStartHour = startHour;
        mStartMinute = startMinute;
        mEndHour = endHour;
        mEndMinute = endMinute;
        this.days = days;
        mQuarter = quarter;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getBuildingLocation() {
        return mBuildingLocation;
    }

    public void setBuildingLocation(int buildingLocation) {
        this.mBuildingLocation = buildingLocation;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        this.mNote = note;
    }

    public int getStartHour() { return mStartHour; }

    public int getStartMinute() { return mStartMinute; }

    public int getEndHour() { return mEndHour; }

    public int getEndMinute() { return mEndMinute; }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public boolean getDay(int index) {
        return days[index];
    }
}
