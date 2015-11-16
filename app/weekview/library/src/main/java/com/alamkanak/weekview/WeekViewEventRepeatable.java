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

    private long mRepeatableId = -1;
    boolean enabled = true;

    // not used, included for Parse
    public WeekViewEventRepeatable() {

    }

    public WeekViewEventRepeatable(String name, int buildingLocation, String location, String note,
                                   long repeatableId, int startHour, int startMinute, int endHour, int endMinute,
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
        mRepeatableId = repeatableId;
        put("repeatableId", mRepeatableId);

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
        if(mName == null)
            mName = getString("name");
        return mName;
    }

    public void setName(String name) {

        this.mName = name;
        put("name", mName);
    }

    public int getBuildingLocation() {

        if(mBuildingLocation == 0)
            mBuildingLocation = getInt("buildingLocation");
        return mBuildingLocation;
    }

    public void setBuildingLocation(int buildingLocation) {
        this.mBuildingLocation = buildingLocation;
        put("buildingLocation", mBuildingLocation);
    }

    public String getBuildingNumber() {
        return mLocation.substring(mLocation.indexOf(' ')+1);
    }

    public String getLocation() {

        if(mLocation == null)
            mLocation = getString("location");
        return mLocation;
    }

    public void setLocation(String location) {

        this.mLocation = location;
        put("location", mLocation);
    }

    public String getNote() {

        if(mNote == null)
            mNote = getString("note");
        return mNote;
    }

    public void setNote(String note) {

        this.mNote = note;
        put("note", mNote);
    }

    public int getStartHour() {

        mStartHour = getInt("startHour");
        return mStartHour;
    }

    public int getStartMinute() {

        mStartMinute = getInt("startMinute");
        return mStartMinute;
    }

    public int getEndHour() {

        mEndHour = getInt("endHour");
        return mEndHour;
    }

    public int getEndMinute() {

        mEndMinute = getInt("endMinute");
        return mEndMinute;
    }

    public int getColor() {

        if(mColor == 0)
            mColor = getInt("color");
        return mColor;
    }

    public void setColor(int color) {

        this.mColor = color;
        put("color", mColor);
    }

    public boolean getDay(int index) {
        switch(index) {
            case 0:
                sunday = getBoolean("sunday");
                return sunday;
            case 1:
                monday = getBoolean("monday");
                return monday;
            case 2:
                tuesday = getBoolean("tuesday");
                return tuesday;
            case 3:
                wednesday = getBoolean("wednesday");
                return wednesday;
            case 4:
                thursday = getBoolean("thursday");
                return thursday;
            case 5:
                friday = getBoolean("friday");
                return friday;
            case 6:
                saturday = getBoolean("saturday");
                return saturday;
            default:
                return false;
        }
    }

    public void setRepeatableId(long repeatableId) {

        this.mRepeatableId = repeatableId;
        put("repeatableId", mRepeatableId);

    }

    public long getRepeatableId() {

        if(getLong("repeatableId") == -1)
            mRepeatableId = getLong("repeatableId");

        return mRepeatableId;
    }
}
