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

    // store the date and time of the event
    private int year, month, day;
    private int startHour, startMinute;
    private int endHour, endMinute;
    private int durationMinutes;
    private boolean start;

    // the TextViews in the layout
    TextView startTime, endTime;
    TextView dateView;

    // the event we are currently editing
    private static WeekViewEvent currEvent = null;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // set the building spinner
        String[] buildingsList = getResources().getStringArray(R.array.buildingsArray);
        Spinner buildings = (Spinner) findViewById(R.id.buildingLocation);
        adapter = new ArrayAdapter<String>(EditEventActivity.this,
                android.R.layout.simple_spinner_item, buildingsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        buildings.setAdapter(adapter);
        buildings.setOnItemSelectedListener(this);

        // retrieve the selected event from the ScheduleActivity
        currEvent = ScheduleActivity.getCurrentEvent();

        // set the spinner value
        int index = 0;
        for (int i=0;i<buildings.getCount();i++){
            if (buildings.getItemAtPosition(i).toString().equalsIgnoreCase(currEvent.getBuildingLocation())){
                index = i;
                break;
            }
        }
        buildings.setSelection(index);

        // set the TextViews
        startTime = (TextView)findViewById(R.id.textView);
        endTime = (TextView)findViewById(R.id.textView3);
        dateView = (TextView)findViewById(R.id.textView2);

        // set the EditTexts
        EditText title, location, note;
        title = (EditText) findViewById(R.id.name);
        location = (EditText) findViewById(R.id.location);
        note = (EditText) findViewById(R.id.note);
        title.setText(currEvent.getName());
        location.setText(currEvent.getBuildingNumber());
        note.setText(currEvent.getNote());

        // assigns the year, month, and day fields
        updateDate(currEvent.getStartTime().get(Calendar.YEAR),
                // Calendar is 0-11, our month is 1-12
                currEvent.getStartTime().get(Calendar.MONTH)+1,
                currEvent.getStartTime().get(Calendar.DAY_OF_MONTH));

        startHour = currEvent.getStartTime().get(Calendar.HOUR_OF_DAY);
        startMinute = currEvent.getStartTime().get(Calendar.MINUTE);
        endHour = currEvent.getEndTime().get(Calendar.HOUR_OF_DAY);
        endMinute = currEvent.getEndTime().get(Calendar.MINUTE);

        durationMinutes = (endHour-startHour)*60 + endMinute - startMinute;

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

        /*
         * How to use the checkboxes:
         * CheckBox sunday = (CheckBox) findViewById(R.id.checkBoxSun);
         * daysOfWeek[0] = (sunday.isChecked());
         */

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
            location += " " + eventLocation.getText().toString() ;
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

    @SuppressWarnings("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the original date as the default date in the picker, and return a DatePicker with
            // that date. DatePicker months are 0-11, while our months are 1-12
            return new DatePickerDialog(getActivity(), this, year, month-1, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // DatePicker months are 0-11, while our months are 1-12
            updateDate(year, month+1, day);
        }
    }

    @SuppressWarnings("ValidFragment")
    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the original event time as the default values for the picker
            int hour, minute;

            if (start) {
                hour = startHour;
                minute = startMinute;
            }
            else {
                hour = endHour;
                minute = endMinute;
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
        this.month = month;
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
            if(endMinute < 10)
                endTime.setText(endHour + ":0" + endMinute);
            else
                endTime.setText(endHour + ":" + endMinute);
        }
        else {
            endHour = hourOfDay + durationMinutes/60;
            endMinute = minute + durationMinutes%60;
            if(endMinute >= 60) {
                endHour++;
                endMinute -= 60;
            }
            if(endMinute < 10)
                endTime.setText(endHour + ":0" + endMinute);
            else
                endTime.setText(endHour + ":" + endMinute);
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
