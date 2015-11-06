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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.text.format.DateFormat;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import java.util.Calendar;

public class InputEventActivity extends AppCompatActivity {

    private int year, month, day;
    private int startHour, startMinute;
    private int endHour, endMinute;
    private boolean start;
    TextView startTime, endTime;
    TextView dateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_event);

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

                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

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
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            updateTime(hourOfDay, minute);
        }

    }

    public void updateDate( int year, int month, int day ) {
        this.year = year;
        this.month = month+1;
        this.day = day;
        dateView.setText(this.month + "/" + day + "/" + year);
    }

    public void updateTime( int hourOfDay, int minute) {
        if(start) {
            startHour = hourOfDay;
            startMinute = minute;
            if(minute < 10)
                startTime.setText(hourOfDay + ":0" + minute);
            else
                startTime.setText(hourOfDay + ":" + minute);
        }
        else
        {
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