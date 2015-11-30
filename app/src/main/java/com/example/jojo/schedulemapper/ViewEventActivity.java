package com.example.jojo.schedulemapper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.Calendar;

/**
 * Created by Lucy on 11/22/2015.
 */
public class ViewEventActivity extends AppCompatActivity {

    private int year, month, day;
    private int startHour, startMinute;
    private int endHour, endMinute;
    TextView startTime, endTime;
    TextView dateView;
    private static WeekViewEvent currEvent = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        // get the event to view
        currEvent = ScheduleActivity.getCurrentEvent();

        // find the text fields
        startTime = (TextView)findViewById(R.id.textView);
        endTime = (TextView)findViewById(R.id.textView3);
        dateView = (TextView)findViewById(R.id.textView2);

        // set the text fields to the current location's information
        TextView title, location, note;
        title = (TextView) findViewById(R.id.name);
        location = (TextView) findViewById(R.id.location);
        note = (TextView) findViewById(R.id.note);
        title.setText(currEvent.getName());
        location.setText(currEvent.getLocation());
        note.setText(currEvent.getNote());

        year = currEvent.getStartTime().get(Calendar.YEAR);
        month = currEvent.getStartTime().get(Calendar.MONTH);
        day = currEvent.getStartTime().get(Calendar.DAY_OF_MONTH);
        updateDate(year, month, day);

        startHour = currEvent.getStartTime().get(Calendar.HOUR);
        startMinute = currEvent.getStartTime().get(Calendar.MINUTE);
        updateStartTime(startHour, startMinute);

        endHour = currEvent.getEndTime().get(Calendar.HOUR);
        endMinute = currEvent.getEndTime().get(Calendar.MINUTE);
        updateEndTime(endHour, endMinute);


        CustomFontHelper changeTitleFont = new CustomFontHelper();

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.startButton),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.endButton),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.dateButton),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.name),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.textView),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.textView2),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.textView3),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.note),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.location),
                "fonts/Roboto-Light.ttf", getApplicationContext());

    }

    public void updateDate( int year, int month, int day ) {
        this.year = year;
        this.month = month+1;
        this.day = day;
        dateView.setText(this.month + "/" + day + "/" + year);
    }

    public void updateStartTime( int hourOfDay, int minute) {
        startHour = hourOfDay;
        startMinute = minute;
        if (minute < 10)
            startTime.setText(hourOfDay + ":0" + minute);
        else
            startTime.setText(hourOfDay + ":" + minute);
    }

    public void updateEndTime( int hourOfDay, int minute ) {
        endHour = hourOfDay;
        endMinute = minute;
        if(minute < 10)
            endTime.setText(hourOfDay + ":0" + minute);
        else
            endTime.setText(hourOfDay + ":" + minute);
    }
}
