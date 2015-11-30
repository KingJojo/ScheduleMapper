package com.example.jojo.schedulemapper;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEventRepeatable;

/**
 * Created by Nathan on 11/14/2015.
 * Activity class to edit a repeatable event. Called from the schedule activity on a long press
 * of a repeatable event.
 */
public class EditRepeatableEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // store the time of the event
    private int startHour, startMinute;
    private int endHour, endMinute;
    private int durationMinutes;
    private boolean start;

    // the TextViews in the layout
    TextView startTime, endTime;
    TextView dateView;

    // the event we are currently editing
    private static WeekViewEventRepeatable currEvent = null;
    ArrayAdapter<String> adapter;

    // booleans to store whether it is repeating or not
    private boolean sunday, monday, tuesday, wednesday, thursday, friday, saturday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_repeatable_event);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // set the buildings spinner
        String[] buildingsList = getResources().getStringArray(R.array.buildingsArray);
        Spinner buildings = (Spinner) findViewById(R.id.buildingLocation);
        adapter = new ArrayAdapter<String>(EditRepeatableEventActivity.this,
                android.R.layout.simple_spinner_item, buildingsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        buildings.setAdapter(adapter);
        buildings.setOnItemSelectedListener(this);

        // retrieve the selected event from the ScheduleActivity
        currEvent = ScheduleActivity.getCurrentRepeatableEvent();

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

        // assign the times
        startHour = currEvent.getStartHour();
        startMinute = currEvent.getStartMinute();
        endHour = currEvent.getEndHour();
        endMinute = currEvent.getEndMinute();
        durationMinutes = (endHour-startHour)*60 + endMinute - startMinute;

        // update times
        start = true;
        updateStartTime(startHour, startMinute);
        start = false;
        updateEndTime(endHour, endMinute);

        // get and assign checkbox values
        sunday = currEvent.getDay(0);
        monday = currEvent.getDay(1);
        tuesday = currEvent.getDay(2);
        wednesday = currEvent.getDay(3);
        thursday = currEvent.getDay(4);
        friday = currEvent.getDay(5);
        saturday = currEvent.getDay(6);


        /* Set fonts correctly */
        CustomFontHelper changeTitleFont = new CustomFontHelper();

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.startButton),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.endButton),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.editEventButton),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.deleteEventButton),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.textView),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.textView3),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.note),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.location),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        changeTitleFont.setCustomFont((TextView) findViewById(R.id.name),
                "fonts/Roboto-Light.ttf", getApplicationContext());

        // find checkboxes
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

    // called when user pressed Edit event button
    public void editEvent(View view)
    {
        Intent intent = new Intent();

        // set text and spinner objects
        EditText eventText = (EditText) findViewById(R.id.name);
        EditText eventLocation = (EditText) findViewById(R.id.location);
        EditText eventNote = (EditText) findViewById(R.id.note);
        Spinner building = (Spinner) findViewById(R.id.buildingLocation);

        // check for errors and toast the user
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

            // if no errors, load up the data into the intent
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

            // return the intent
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    // return a null intent if delete is pressed
    public void deleteEvent(View view) {
        setResult(RESULT_OK, null);
        finish();
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

        // after time is set by user, update text fields
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            updateStartTime(hourOfDay, minute);
            updateEndTime(hourOfDay, minute);
        }

    }

    // update the start time text and corresponding variables
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

    // update end time text and corresponding variables
    public void updateEndTime( int hourOfDay, int minute ) {
        // set end time to user inputted time
        if (!start) {
            endHour = hourOfDay;
            endMinute = minute;
            if(endMinute < 10)
                endTime.setText(endHour + ":0" + endMinute);
            else
                endTime.setText(endHour + ":" + endMinute);
        }
        // set end time to start time + Event duration
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
