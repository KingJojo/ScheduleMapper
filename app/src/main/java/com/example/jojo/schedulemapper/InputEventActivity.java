package com.example.jojo.schedulemapper;

/*
 * Created by kevinkuo on 11/1/15
 */
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.text.format.DateFormat;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.content.Context;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioButton;
import android.widget.LinearLayout;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.Calendar;

public class InputEventActivity extends AppCompatActivity implements OnItemSelectedListener{

    private int year, month, day;
    private int startHour, startMinute;
    private int endHour, endMinute;
    private boolean start;
    TextView startTime, endTime;
    TextView dateView;
    private static WeekViewEvent currEvent = null;
    private LinearLayout myLayout = null;
    private View hiddenInfo = null;
    private ArrayAdapter<String> adapter;
    private boolean repeatable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_event);

        String[] buildingsList = {"CSE", "Center", "WLH", "LEDDN", "PRICE", "York",
                                    "Galbraith", "Peterson", "Cogs", "Sequoyah", "AP&M",
                                        "Mandler", "McGill", "SOLIS"};
        Spinner buildings = (Spinner) findViewById(R.id.buildingLocation);
        adapter = new ArrayAdapter<String>(InputEventActivity.this,
                    android.R.layout.simple_spinner_item, buildingsList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        buildings.setAdapter(adapter);
        buildings.setOnItemSelectedListener(this);
        currEvent = ScheduleActivity.getCurrentEvent();
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id){}
    public void onNothingSelected(AdapterView<?> parent){}

    public void submitEvent(View view)
    {
        Intent intent = new Intent();

        EditText eventText = (EditText) findViewById(R.id.name);
        EditText eventLocation = (EditText) findViewById(R.id.location);
        EditText eventNote = (EditText) findViewById(R.id.note);
        Spinner building = (Spinner) findViewById(R.id.buildingLocation);

/*      How to use the checkboxes:
        CheckBox sunday = (CheckBox) findViewById(R.id.checkBoxSun);
        daysOfWeek[0] = (sunday.isChecked());
        } */

        if(eventText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter an event title.", Toast.LENGTH_SHORT).show();
        } else if(eventLocation.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter an event location.", Toast.LENGTH_SHORT).show();
        } else if(dateView != null && dateView.getText().toString().equals("No date selected")) {
            Toast.makeText(getApplicationContext(), "Please enter an event date.", Toast.LENGTH_SHORT).show();
        } else if(startTime.getText().toString().equals("No time selected")) {
            Toast.makeText(getApplicationContext(), "Please enter an event start time.", Toast.LENGTH_SHORT).show();
        } else if(endTime.getText().toString().equals("No time selected")) {
            Toast.makeText(getApplicationContext(), "Please enter an event end time.", Toast.LENGTH_SHORT).show();
        } else if(startHour > endHour || (startHour == endHour && startMinute > endMinute)) {
            Toast.makeText(getApplicationContext(), "Please enter a valid start and end time.", Toast.LENGTH_SHORT).show();
        } else {
            String location = building.getSelectedItem().toString();
            location += " " + eventLocation.getText().toString();
            intent.putExtra("eventTitle", eventText.getText().toString());
            intent.putExtra("locationBuilding", building.getSelectedItem().toString());
            intent.putExtra("location", location);
            intent.putExtra("repeatable", repeatable);
            intent.putExtra("note", eventNote.getText().toString());
            intent.putExtra("year", year);
            intent.putExtra("month", month);
            intent.putExtra("day", day);
            intent.putExtra("startHour", startHour);
            intent.putExtra("startMinute", startMinute);
            intent.putExtra("endHour", endHour);
            intent.putExtra("endMinute", endMinute);

            if (repeatable) {
                boolean days[] = new boolean[7];

                days[0] = ((CheckBox)findViewById(R.id.checkBoxSun)).isChecked();
                days[1] = ((CheckBox)findViewById(R.id.checkBoxMon)).isChecked();
                days[2] = ((CheckBox)findViewById(R.id.checkBoxTue)).isChecked();
                days[3] = ((CheckBox)findViewById(R.id.checkBoxWed)).isChecked();
                days[4] = ((CheckBox)findViewById(R.id.checkBoxThu)).isChecked();
                days[5] = ((CheckBox)findViewById(R.id.checkBoxFri)).isChecked();
                days[6] = ((CheckBox)findViewById(R.id.checkBoxSat)).isChecked();

                intent.putExtra("days", days);
            }

            setResult(RESULT_OK, intent);
            finish();
        }
    }
    public void onRadioButtonClicked(View view)
    {
        boolean checked = ((RadioButton) view).isChecked();
        myLayout = (LinearLayout) findViewById(R.id.forRepeating);

        switch(view.getId())
        {
            case R.id.repeatableYes:
                if(checked){
                    if(hiddenInfo != null) {
                        myLayout.removeView(hiddenInfo);
                    }

                    hiddenInfo = getLayoutInflater().inflate(R.layout.activity_input_repeatable
                            , myLayout, false);
                    myLayout.addView(hiddenInfo);
                    startTime = (TextView)findViewById(R.id.textView);
                    endTime = (TextView)findViewById(R.id.textView3);
                    repeatable = true;
                }
                break;
            case R.id.repeatableNo:
                if(checked){
                    if(hiddenInfo != null) {
                        myLayout.removeView(hiddenInfo);
                    }
                    hiddenInfo = getLayoutInflater().inflate(R.layout.activity_input_nonrepeatable
                            , myLayout, false);
                    myLayout.addView(hiddenInfo);
                    dateView = (TextView)findViewById(R.id.textView2);
                    startTime = (TextView)findViewById(R.id.textView);
                    endTime = (TextView)findViewById(R.id.textView3);
                    repeatable = false;
                }
                break;
        }
    }


    @SuppressLint("ValidFragment")
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

    @SuppressLint("ValidFragment")
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
                if (start) {
                    hour = c.get(Calendar.HOUR_OF_DAY);
                    minute = 0;
                }
                else {
                    hour = startHour + 1;
                    minute = startMinute;
                }
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