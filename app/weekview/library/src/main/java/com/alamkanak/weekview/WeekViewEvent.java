package com.alamkanak.weekview;

import java.util.Calendar;
import java.util.Date;

import android.graphics.Color;
import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Raquib-ul-Alam Kanak on 7/21/2014.
 * Modified by Jojo Chen, Kevin Kuo, Lucy Li, Nathan Ng, Thomas Gui on 11/16/2015.
 * Website: http://april-shower.com
 */

@ParseClassName("WeekViewEvent")
public class WeekViewEvent extends ParseObject{
    private long mId;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private String mName;
    private String mLocation;
    private String mNote;
    private int mColor;
    private String mBuildingLocation;
    private int prevColor;
    private long mRepeatableId = - 1;
    boolean enabled = true;

    // not used, included for Parse
    public WeekViewEvent(){

    }

    /*
     * Purpose: main constructor used to input events into the calendar
     *
     * Parameters:
     * @long id: WeekView ID, must be unique
     * @String name: Name of event to be displayed
     * @int buildingLocation: Building according to stored array of buildings
     * @String location: Room number
     * @String note: Optional note to be attached to event
     * @boolean repeatable: Determines whether not event is single event or a populated event from
     *          WeekViewEventRepeatable
     * @int start_____: the start date and time stored in a Calendar object
     * @int end_____: the end date and time stored in a Calendar object
     */
    public WeekViewEvent(long id, String name, String buildingLocation, String location,
                         String note, long repeatable, int startYear, int startMonth,
                         int startDay, int startHour, int startMinute, int endYear, int endMonth,
                         int endDay, int endHour, int endMinute, boolean enabled) {

        prevColor = Color.parseColor("#e6e6e6");
        put("prevColor", prevColor);

        this.mId = id;
        put("eventId", mId);

        this.mName = name;
        put("name", mName);

        this.mBuildingLocation = buildingLocation;
        put("buildingLocation", mBuildingLocation);

        this.mLocation = location;
        put("location", mLocation);

        this.mRepeatableId = repeatable;

        this.mNote = note;
        put("note", mNote);

        this.enabled = enabled;
        put("enabled", enabled);

        if(enabled == false) {
            int temp = getColor();
            mColor = getPrevColor();
            prevColor = temp;
            put("color", mColor);
            put("prevColor", prevColor);
        }

        this.mStartTime = Calendar.getInstance();
        this.mStartTime.set(Calendar.YEAR, startYear);
        this.mStartTime.set(Calendar.MONTH, startMonth-1);
        this.mStartTime.set(Calendar.DAY_OF_MONTH, startDay);
        this.mStartTime.set(Calendar.HOUR_OF_DAY, startHour);
        this.mStartTime.set(Calendar.MINUTE, startMinute);
        put("startTime", mStartTime.getTime());

        this.mEndTime = Calendar.getInstance();
        this.mEndTime.set(Calendar.YEAR, endYear);
        this.mEndTime.set(Calendar.MONTH, endMonth-1);
        this.mEndTime.set(Calendar.DAY_OF_MONTH, endDay);
        this.mEndTime.set(Calendar.HOUR_OF_DAY, endHour);
        this.mEndTime.set(Calendar.MINUTE, endMinute);
        put("endTime", mEndTime.getTime());

    }

    /**
     * Initializes the event for week view.
     * @param id The id of the event.
     * @param name Name of the event.
     * @param location The location of the event.
     * @param startTime The time when the event starts.
     * @param endTime The time when the event ends.
     */
    public WeekViewEvent(long id, String name, String location, Calendar startTime, Calendar endTime) {
        this.mId = id;
        this.mName = name;
        this.mLocation = location;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
    }

    /**
     * Initializes the event for week view.
     * @param id The id of the event.
     * @param name Name of the event.
     * @param startTime The time when the event starts.
     * @param endTime The time when the event ends.
     */
    public WeekViewEvent(long id, String name, Calendar startTime, Calendar endTime) {
        this(id, name, null, startTime, endTime);
    }

    public static Calendar dateToCal(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
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

    public String getName() {
        if(mName == null)
            mName = getString("name");
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
        put("name", mName);
    }

    public String getBuildingLocation() {
        if(mBuildingLocation == null)
            mBuildingLocation = getString("buildingLocation");
        return mBuildingLocation;
    }

    public void setBuildingLocation(String buildingLocation) {
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

    public int getColor() {
        if(mColor == 0)
            mColor = getInt("color");
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
        put("color", mColor);
    }

    public long getId() {
        if(mId == 0)
            mId = getLong("eventId");
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
        put("eventId", mId);
    }

    public int getPrevColor() {
        if(prevColor == 0)
            prevColor = getInt("prevColor");
        return prevColor;
    }

    public void changeColor() {
        int temp = getColor();
        mColor = getPrevColor();
        prevColor = temp;
        put("color", mColor);
        put("prevColor", prevColor);
        enabled = !enabled;
        put("enabled", enabled);
    }

    public boolean isEnabled() {
        enabled = getBoolean("enabled");
        return enabled;
    }

    public boolean isRepeatable(){ return (mRepeatableId != -1); }

    public void setRepeatableId(long repeatableId) {

        this.mRepeatableId = repeatableId;

    }

    public long getRepeatableId() {

        return mRepeatableId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeekViewEvent that = (WeekViewEvent) o;

        return mId == that.mId;

    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }
}
