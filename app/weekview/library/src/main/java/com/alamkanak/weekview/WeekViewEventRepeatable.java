package com.alamkanak.weekview;

import android.graphics.Color;
import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by kevinkuo on 11/15/15.
 */
@ParseClassName("WeekViewEventRepeatable")
public class WeekViewEventRepeatable extends ParseObject{
    private int mStartHour, mStartMinute;
    private int mEndHour, mEndMinute;
    private String mName;
    private String mLocation;
    private String mNote;
    private int mColor;
    private int mBuildingLocation;
    private int prevColor;
    //private String mQuarter;
    private boolean sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    boolean enabled = true;

    public WeekViewEventRepeatable() {

    }

    public WeekViewEventRepeatable(String name, int buildingLocation, String location, String note,
                                   int startHour, int startMinute, int endHour, int endMinute,
                                   boolean days[], String quarter) {

        prevColor = Color.parseColor("#e6e6e6");
        put("prevColor", prevColor);

        mName = name;
        put("name", mName);
        mBuildingLocation = buildingLocation;
        put("buildingLocation", mBuildingLocation);
        mLocation = location;
        put("location", mLocation);
        mNote = note;
        put("note", mNote);

        mStartHour = startHour;
        put("startHour", mStartHour);
        mStartMinute = startMinute;
        put("startMinute", mStartMinute);
        mEndHour = endHour;
        put("endHour", mEndHour);
        mEndMinute = endMinute;
        put("endMinute", mEndMinute);

        sunday = days[0];
        monday = days[1];
        tuesday = days[2];
        wednesday = days[3];
        thursday = days[4];
        friday = days[5];
        saturday = days[6];
        put("sunday", sunday);
        put("monday", monday);
        put("tuesday", tuesday);
        put("wednesday", wednesday);
        put("thursday", thursday);
        put("friday", friday);
        put("saturday", saturday);

        //mQuarter = quarter;
        //put("quarter", mQuarter);
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
        switch(index) {
            case 0: return sunday;
            case 1: return monday;
            case 2: return tuesday;
            case 3: return wednesday;
            case 4: return thursday;
            case 5: return friday;
            case 6: return saturday;
        }
        return false;
    }
}
