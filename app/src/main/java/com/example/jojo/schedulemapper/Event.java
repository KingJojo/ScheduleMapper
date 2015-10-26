package com.example.jojo.schedulemapper;

/**
 * Created by nathanng on 10/26/15.
 */
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseGeoPoint;

@ParseClassName("Event")
public class Event extends ParseObject {

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public String getStartTime() {
        return getString("startTime");
    }

    public void setStartTime(String startTime) {
        put("startTime", startTime);
    }

    public String getEndTime() {
        return getString("endTime");
    }

    public void setEndTime(String endTime) {
        put("endTime", endTime);
    }

    public static ParseQuery<Event> getQuery() {
        return ParseQuery.getQuery(Event.class);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }
}
