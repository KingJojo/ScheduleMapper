package com.example.jojo.schedulemapper;

/*
 * Created by kevinkuo on 11/1/15
 */
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.text.format.DateFormat;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.Calendar;

public class InputEventActivity extends AppCompatActivity {

    private int year, month, day;
    private int startHour, startMinute;
    private int endHour, endMinute;
    private boolean start;
    TextView startTime, endTime;
    TextView dateView;
    private static WeekViewEvent currEvent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_event);

        currEvent = ScheduleActivity.getCurrentEvent();

        startTime = (TextView)findViewById(R.id.textView);
        endTime = (TextView)findViewById(R.id.textView3);
        dateView = (TextView)findViewById(R.id.textView2);
        Button button = (Button) findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();

                EditText eventText = (EditText) findViewById(R.id.name);
                EditText eventLocation = (EditText) findViewById(R.id.location);
                EditText eventNote = (EditText) findViewById(R.id.note);

                CheckBox sunday = (CheckBox) findViewById(R.id.checkBoxSun);
                CheckBox monday = (CheckBox) findViewById(R.id.checkBoxMon);
                CheckBox tuesday = (CheckBox) findViewById(R.id.checkBoxTues);
                CheckBox wednesday = (CheckBox) findViewById(R.id.checkBoxWed);
                CheckBox thursday = (CheckBox) findViewById(R.id.checkBoxThurs);
                CheckBox friday = (CheckBox) findViewById(R.id.checkBoxFri);
                CheckBox saturday = (CheckBox) findViewById(R.id.checkBoxSat);

                // get values from checkboxes
                boolean[] daysOfWeek = new boolean[7];
                daysOfWeek[0] = (sunday.isChecked());
                daysOfWeek[1] = (monday.isChecked());
                daysOfWeek[2] = (tuesday.isChecked());
                daysOfWeek[3] = (wednesday.isChecked());
                daysOfWeek[4] = (thursday.isChecked());
                daysOfWeek[5] = (friday.isChecked());
                daysOfWeek[6] = (saturday.isChecked());
                for ( boolean i : daysOfWeek ) {
                    System.out.println(i);
                }

                intent.putExtra("eventTitle", eventText.getText().toString());
                intent.putExtra("location", eventLocation.getText().toString());
                intent.putExtra("note", eventNote.getText().toString());
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                intent.putExtra("day", day);
                intent.putExtra("startHour", startHour);
                intent.putExtra("startMinute", startMinute);
                intent.putExtra("endHour", endHour);
                intent.putExtra("endMinute", endMinute);
                intent.putExtra("daysOfWeek", daysOfWeek);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        if (currEvent != null) {
            EditText title, location, note;
            title = (EditText) findViewById(R.id.name);
            location = (EditText) findViewById(R.id.location);
            note = (EditText) findViewById(R.id.note);
            title.setText(currEvent.getName());
            location.setText(currEvent.getLocation());
            note.setText(currEvent.getNote());
            int year = currEvent.getStartTime().get(Calendar.YEAR);
            int month = currEvent.getStartTime().get(Calendar.MONTH);
            int day = currEvent.getStartTime().get(Calendar.DAY_OF_MONTH);
            updateDate(year, month, day);
            int startHour = currEvent.getStartTime().get(Calendar.HOUR);
            int startMinute = currEvent.getStartTime().get(Calendar.MINUTE);
            int endHour = currEvent.getEndTime().get(Calendar.HOUR);
            int endMinute = currEvent.getEndTime().get(Calendar.MINUTE);
            start = true;
            updateStartTime(startHour, startMinute);
            start = false;
            updateEndTime(endHour, endMinute);
        }

    }

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year, month, day;
            if (currEvent != null) {
                year = currEvent.getStartTime().get(Calendar.YEAR);
                month = currEvent.getStartTime().get(Calendar.MONTH);
                day = currEvent.getStartTime().get(Calendar.DAY_OF_MONTH);
                updateDate(year, month, day);
            }
            else {
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            updateDate(year, month, day);
        }
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour, minute;
            if (currEvent != null) {
                hour = currEvent.getStartTime().get(Calendar.HOUR);
                minute = currEvent.getStartTime().get(Calendar.MINUTE);
            }
            else {
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = 0;
            }

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            updateStartTime(hourOfDay, minute);
            updateEndTime(hourOfDay, minute);
        }

    }

    public void updateDate( int year, int month, int day ) {
        this.year = year;
        this.month = month+1;
        this.day = day;
        dateView.setText(this.month + "/" + day + "/" + year);
    }

    public void updateStartTime( int hourOfDay, int minute) {
        if (start) {
            startHour = hourOfDay;
            startMinute = minute;
            if (minute < 10)
                startTime.setText(hourOfDay + ":0" + minute);
            else
                startTime.setText(hourOfDay + ":" + minute);
        }
    }

    public void updateEndTime( int hourOfDay, int minute ) {
        if (!start) {
            endHour = hourOfDay;
            endMinute = minute;
            if(minute < 10)
                endTime.setText(hourOfDay + ":0" + minute);
            else
                endTime.setText(hourOfDay + ":" + minute);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showStartTimePicker (View v) {
        start = true;
        showTimePickerDialog(v);
    }

    public void showEndTimePicker (View v) {
        start = false;
        showTimePickerDialog(v);
    }
}