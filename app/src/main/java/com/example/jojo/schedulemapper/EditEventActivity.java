package com.example.jojo.schedulemapper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.Calendar;

/**
 * Created by Lucy on 11/14/2015.
 */
public class EditEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private int year, month, day;
    private int startHour, startMinute;
    private int endHour, endMinute;
    private boolean start;
    TextView startTime, endTime;
    TextView dateView;
    private static WeekViewEvent currEvent = null;
    private LinearLayout myLayout = null;
    private View hiddenInfo = null;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        String[] buildingsList = getResources().getStringArray(R.array.buildingsArray);
        Spinner buildings = (Spinner) findViewById(R.id.buildingLocation);
        adapter = new ArrayAdapter<String>(EditEventActivity.this,
                android.R.layout.simple_spinner_item, buildingsList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        buildings.setAdapter(adapter);
        buildings.setOnItemSelectedListener(this);

        currEvent = ScheduleActivity.getCurrentEvent();

        int index = 0;

        for (int i=0;i<buildings.getCount();i++){
            if (buildings.getItemAtPosition(i).toString().equalsIgnoreCase(currEvent.getBuildingLocation())){
                index = i;
                break;
            }
        }

        buildings.setSelection(index);

        startTime = (TextView)findViewById(R.id.textView);
        endTime = (TextView)findViewById(R.id.textView3);
        dateView = (TextView)findViewById(R.id.textView2);

        EditText title, location, note;
        title = (EditText) findViewById(R.id.name);
        location = (EditText) findViewById(R.id.location);
        note = (EditText) findViewById(R.id.note);
        title.setText(currEvent.getName());
        location.setText(currEvent.getBuildingNumber());
        note.setText(currEvent.getNote());
        year = currEvent.getStartTime().get(Calendar.YEAR);
        month = currEvent.getStartTime().get(Calendar.MONTH);
        day = currEvent.getStartTime().get(Calendar.DAY_OF_MONTH);
        updateDate(year, month, day);
        startHour = currEvent.getStartTime().get(Calendar.HOUR_OF_DAY);
        startMinute = currEvent.getStartTime().get(Calendar.MINUTE);
        endHour = currEvent.getEndTime().get(Calendar.HOUR_OF_DAY);
        endMinute = currEvent.getEndTime().get(Calendar.MINUTE);
        start = true;
        updateStartTime(startHour, startMinute);
        start = false;
        updateEndTime(endHour, endMinute);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id){}
    public void onNothingSelected(AdapterView<?> parent){}

    public void editEvent(View view)
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
            String location =building.getSelectedItem().toString() ;
            location += " " +eventLocation.getText().toString() ;
            intent.putExtra("eventTitle", eventText.getText().toString());
            intent.putExtra("locationBuilding", building.getSelectedItem().toString());
            intent.putExtra("location", location);
            intent.putExtra("note", eventNote.getText().toString());
            intent.putExtra("year", year);
            intent.putExtra("month", month);
            intent.putExtra("day", day);
            intent.putExtra("startHour", startHour);
            intent.putExtra("startMinute", startMinute);
            intent.putExtra("endHour", endHour);
            intent.putExtra("endMinute", endMinute);
            intent.putExtra("repeatable", false);

            System.out.println("Finishing up edit");
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void deleteEvent(View view) {
        setResult(RESULT_OK, null);
        finish();
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
                if (start) {
                    hour = currEvent.getStartTime().get(Calendar.HOUR_OF_DAY);
                    minute = currEvent.getStartTime().get(Calendar.MINUTE);
                }
                else {
                    hour = currEvent.getEndTime().get(Calendar.HOUR_OF_DAY);
                    minute = currEvent.getEndTime().get(Calendar.MINUTE);
                }
            }
            else {
                hour = c.get(Calendar.HOUR_OF_DAY) + 1;
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
