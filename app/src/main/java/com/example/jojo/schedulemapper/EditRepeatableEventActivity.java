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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewEventRepeatable;

import java.util.Calendar;

/**
 * Created by Nathan on 11/14/2015.
 */
public class EditRepeatableEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private int year, month, day;
    private int startHour, startMinute;
    private int endHour, endMinute;
    private boolean start;
    TextView startTime, endTime;
    TextView dateView;
    private static WeekViewEventRepeatable currEvent = null;
    private LinearLayout myLayout = null;
    private View hiddenInfo = null;
    ArrayAdapter<String> adapter;
    private boolean sunday, monday, tuesday, wednesday, thursday, friday, saturday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_repeatable_event);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        String[] buildingsList = getResources().getStringArray(R.array.buildingsArray);
        Spinner buildings = (Spinner) findViewById(R.id.buildingLocation);
        adapter = new ArrayAdapter<String>(EditRepeatableEventActivity.this,
                android.R.layout.simple_spinner_item, buildingsList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        buildings.setAdapter(adapter);
        buildings.setOnItemSelectedListener(this);

        currEvent = ScheduleActivity.getCurrentRepeatableEvent();

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

        startHour = currEvent.getStartHour();
        startMinute = currEvent.getStartMinute();
        endHour = currEvent.getEndHour();
        endMinute = currEvent.getEndMinute();

        start = true;
        updateStartTime(startHour, startMinute);
        start = false;
        updateEndTime(endHour, endMinute);

        sunday = currEvent.getDay(0);
        monday = currEvent.getDay(1);
        tuesday = currEvent.getDay(2);
        wednesday = currEvent.getDay(3);
        thursday = currEvent.getDay(4);
        friday = currEvent.getDay(5);
        saturday = currEvent.getDay(6);

        ((CheckBox)findViewById(R.id.checkBoxSun)).setChecked(sunday);
        ((CheckBox)findViewById(R.id.checkBoxMon)).setChecked(monday);
        ((CheckBox)findViewById(R.id.checkBoxTue)).setChecked(tuesday);
        ((CheckBox)findViewById(R.id.checkBoxWed)).setChecked(wednesday);
        ((CheckBox)findViewById(R.id.checkBoxThu)).setChecked(thursday);
        ((CheckBox)findViewById(R.id.checkBoxFri)).setChecked(friday);
        ((CheckBox)findViewById(R.id.checkBoxSat)).setChecked(saturday);

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
            intent.putExtra("buildingLocation", building.getSelectedItem().toString());
            intent.putExtra("location", location);
            intent.putExtra("note", eventNote.getText().toString());
            intent.putExtra("startHour", startHour);
            intent.putExtra("startMinute", startMinute);
            intent.putExtra("endHour", endHour);
            intent.putExtra("endMinute", endMinute);


            boolean days[] = new boolean[7];

            days[0] = ((CheckBox)findViewById(R.id.checkBoxSun)).isChecked();
            days[1] = ((CheckBox)findViewById(R.id.checkBoxMon)).isChecked();
            days[2] = ((CheckBox)findViewById(R.id.checkBoxTue)).isChecked();
            days[3] = ((CheckBox)findViewById(R.id.checkBoxWed)).isChecked();
            days[4] = ((CheckBox)findViewById(R.id.checkBoxThu)).isChecked();
            days[5] = ((CheckBox)findViewById(R.id.checkBoxFri)).isChecked();
            days[6] = ((CheckBox)findViewById(R.id.checkBoxSat)).isChecked();

            intent.putExtra("days", days);

            System.out.println("Finishing up edit");
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void deleteEvent(View view) {
        setResult(RESULT_OK, null);
        finish();
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour, minute;
            if (currEvent != null) {
                hour = currEvent.getStartHour();
                minute = currEvent.getStartMinute();
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
