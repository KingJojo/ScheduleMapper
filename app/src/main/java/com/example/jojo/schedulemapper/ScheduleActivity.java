package com.example.jojo.schedulemapper;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Color;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.DisabledRepeatable;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewEventRepeatable;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.FindCallback;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;


/*
 * Created by Raquib-ul-Alam Kanak on 7/21/2014.
 * Website: http://alamkanak.github.io/
 *
 * Modified by Jojo Chen, Kevin Kuo, Lucy Li, Nathan Ng, Thomas Gui
 *
 * Creates the schedule view for the schedule. Displays only one week at a time with buttons to
 * move between the weeks. Events can be added to the schedule using a simple dialog and specifying
 * between single and repeatable events.
 */

public class ScheduleActivity extends AppCompatActivity implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {

    // index of views on the schedule
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;

    // default view is three day
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;

    // the Schedule itself
    private WeekView mWeekView;
    private MenuItem viewMenu;
    private MenuItem editMenu;

    // list of events to keep track of
    private List<WeekViewEvent> events;
    private List<WeekViewEventRepeatable> repeats;
    private List<DisabledRepeatable> disabled;

    // keep track of tapped events, either single or repeatable
    private static WeekViewEvent tappedSingle = null;
    private static WeekViewEventRepeatable tappedRepeat = null;
    private static Calendar tappedStart = null;

    // toggle between edit and view mode
    private boolean editMode = false; // used to toggle between edit and view mode

    // array of colors to use for the events
    private int[] colorArray = {Color.parseColor("#59dbe0"), Color.parseColor("#f57f68"),
                                Color.parseColor("#87d288"), Color.parseColor("#f8b552")};
    private int colorIndex = 0;

    // used to generate ids for the events so there are no collisions
    Random rand = new Random();

    // creates and initializes the schedule and its events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedule);

        // create and initialize the list of single events
        events = new ArrayList<WeekViewEvent>();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("WeekViewEvent");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {

                    // add each event to the event list
                    for (int i = 0; i < eventList.size(); i++) {
                        events.add((WeekViewEvent) eventList.get(i));
                    }

                    // update the data set
                    mWeekView.notifyDatasetChanged();
                } else {
                    // handle Parse Exception here
                }
            }
        });

        // get the repeat list
        repeats = new ArrayList<WeekViewEventRepeatable>();

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("WeekViewEventRepeatable");
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {
                    // add the repeat events to the repeat list
                    for (int i = 0; i < eventList.size(); i++) {
                        repeats.add((WeekViewEventRepeatable) eventList.get(i));
                    }

                    // fill with extra events and update the data set
                    populateRepeatable();
                    mWeekView.notifyDatasetChanged();
                } else {
                    // handle Parse Exception here
                }
            }
        });

        // initialize and fill the list of disabled repeatable events
        disabled = new ArrayList<DisabledRepeatable>();
        ParseQuery<ParseObject> query3 = new ParseQuery<ParseObject>("DisabledRepeatable");
        query3.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {

                    // fill the list and add the new events
                    for (int i = 0; i < eventList.size(); i++) {
                        disabled.add((DisabledRepeatable) eventList.get(i));
                    }
                    populateRepeatable();
                    mWeekView.notifyDatasetChanged();
                } else {
                    // handle Parse Exception here
                }
            }
        });

        // get a reference for the week view in the layout
        mWeekView = (WeekView) findViewById(R.id.weekView);
        // show a toast message about the touched event
        mWeekView.setOnEventClickListener(this);
        // lets WeekView know when month changes
        mWeekView.setMonthChangeListener(this);
        // set long press listener for events
        mWeekView.setEventLongPressListener(this);
        // set listener for the empty schedule
        mWeekView.setEmptyViewLongPressListener(this);
        // tells WeekView to regenerate calendar with current data
        mWeekView.notifyDatasetChanged();
    }

    // inflate the menu bar on the top
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        this.viewMenu = menu.findItem(R.id.change_view);
        this.editMenu = menu.findItem(R.id.edit_menu);
        return true;
    }

    // called when returning from an activity to the Schedule
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if adding an event, request code is `
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                // if no data sent then delete the event
                if(data == null) {
                    if (tappedSingle != null) {
                        tappedSingle.deleteInBackground();
                        events.remove(tappedSingle);
                        mWeekView.notifyDatasetChanged();
                        return;
                    }
                }

                // otherwise remove it and and load the new info into a new event
                if(tappedSingle != null) {
                    events.remove(tappedSingle);
                    tappedSingle.deleteInBackground();
                }

                // retrieve info from returned data
                String title = data.getStringExtra("eventTitle");
                String buildingLocation = data.getStringExtra("locationBuilding");
                String location = data.getStringExtra("location");
                String note = data.getStringExtra("note");

                boolean repeatable = data.getBooleanExtra("repeatable", false);

                int startHour = data.getIntExtra("startHour", 0);
                int startMinute = data.getIntExtra("startMinute", 0);
                int endHour = data.getIntExtra("endHour", 0);
                int endMinute = data.getIntExtra("endMinute", 0);

                // create a WeekViewEventRepeatable if repeat set
                if( repeatable ) {
                    boolean days[] = data.getBooleanArrayExtra("days");

                    WeekViewEventRepeatable newRepeatable = new WeekViewEventRepeatable(title,
                            buildingLocation, location, note, Math.abs(rand.nextLong()), startHour,
                            startMinute, endHour, endMinute, days);

                    newRepeatable.setColor(colorArray[colorIndex]);
                    repeats.add(newRepeatable);
                    newRepeatable.saveInBackground();
                    populateRepeatable();

                    // checks if new repeatable overlaps with events
                    outerloop:
                    for(int i=0; i<events.size(); ++i) {
                        if(events.get(i).getRepeatableId() == newRepeatable.getRepeatableId()){
                            for(int j=0; j<events.size(); ++j) {
                                if(!events.get(i).equals(events.get(j)) && areEventsOverlapping(events.get(i), events.get(j))) {
                                    Toast.makeText(getApplicationContext(), "Warning: New repeatable event " +
                                            "overlaps with existing event.", Toast.LENGTH_SHORT).show();
                                    break outerloop;
                                }
                            }
                        }
                    }

                }

                // create a WeekViewEvent
                else {
                    int year = data.getIntExtra("year", 0);
                    int month = data.getIntExtra("month", 0);
                    int day = data.getIntExtra("day", 0);

                    WeekViewEvent newEvent = new WeekViewEvent(Math.abs(rand.nextLong()), title,
                            buildingLocation, location, note, -1, year, month, day, startHour,
                            startMinute, year, month, day, endHour, endMinute, true);

                    // warning message if new event overlaps with existing event
                    for(int i=0; i<events.size(); ++i){
                        if(areEventsOverlapping(newEvent, events.get(i))){
                            Toast.makeText(getApplicationContext(), "Warning: New single event overlaps with existing event.", Toast.LENGTH_SHORT).show();
                            System.out.println("Overlapping event toast");
                            break;
                        }
                    }

                    // set the color as necessary and add it to the event list
                    newEvent.setColor(colorArray[colorIndex]);
                    events.add(newEvent);
                    newEvent.saveInBackground();

                }

                // update the schedule and color index
                mWeekView.notifyDatasetChanged();
                colorIndex = (colorIndex + 1) % 4;
            }
        }

        // requestCode when editing a WeekViewEventRepeatable
        if(requestCode == 2) {
            if (resultCode == RESULT_OK) {

                // if no data then just delete the event and update
                if(data == null) {
                    if (tappedRepeat != null) {
                        tappedRepeat.deleteInBackground();
                        repeats.remove(tappedRepeat);
                        populateRepeatable();
                        mWeekView.notifyDatasetChanged();
                        return;
                    }
                }

                // otherwise delete and add a new event
                if(tappedRepeat != null) {
                    repeats.remove(tappedRepeat);
                    tappedRepeat.deleteInBackground();
                }

                // get the information from the data
                String title = data.getStringExtra("eventTitle");
                String buildingLocation = data.getStringExtra("buildingLocation");
                String location = data.getStringExtra("location");
                String note = data.getStringExtra("note");

                int startHour = data.getIntExtra("startHour", 0);
                int startMinute = data.getIntExtra("startMinute", 0);
                int endHour = data.getIntExtra("endHour", 0);
                int endMinute = data.getIntExtra("endMinute", 0);

                boolean days[] = data.getBooleanArrayExtra("days");

                // create the new event and add it
                WeekViewEventRepeatable newRepeatable = new WeekViewEventRepeatable(title,
                        buildingLocation, location, note, Math.abs(rand.nextLong()), startHour,
                        startMinute, endHour, endMinute, days);
                newRepeatable.setColor(colorArray[colorIndex]);
                repeats.add(newRepeatable);
                newRepeatable.saveInBackground();
                populateRepeatable();

                mWeekView.notifyDatasetChanged();
                colorIndex = (colorIndex + 1) % 4;

            }
        }
    }

    // called when a menu button is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // checks whether to display day as letter or abbreviation only if view change is called
        if(id == R.id.action_week_view || id == R.id.action_day_view || id == R.id.action_three_day_view) {
            setupDateTimeInterpreter(id == R.id.action_week_view);
        }

        switch (id){

            // change modes
            case R.id.edit_button:
                editMode = true;
                editMenu.setTitle("Edit");
                return true;
            case R.id.view_button:
                editMode = false;
                editMenu.setTitle("View");
                return true;

            // start the add event activity
            case R.id.action_add_event:
                tappedSingle = null;
                tappedStart = null;
                startActivityForResult(new Intent( this, InputEventActivity.class ), 1);
                return true;

            // go to the current day
            case R.id.action_today:
                mWeekView.goToToday();
                return true;

            // increment or decrement the current week
            case R.id.action_next_week:
                mWeekView.incrementWeek();
                return true;
            case R.id.action_prev_week:
                mWeekView.decrementWeek();
                return true;

            // change to different viewing sizes
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    viewMenu.setTitle("1 Day");

                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    viewMenu.setTitle("3 Day");

                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    viewMenu.setTitle("Week");

                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));

                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, 0);

                // format time so that only 24 hour format is printed
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

                return sdf.format(calendar.getTime());
            }
        });
    }

    // simply return all events when month changes
    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) { return events; }

    // fills schedule with events generated from the repeats list that are cleared upon next populate
    private void populateRepeatable(){

        // remove all existing repeatable generated events
        for (int i = events.size() - 1; i >= 0; --i) {
            if (events.get(i).isRepeatable())
                events.remove(i);
        }

        // update data set
        mWeekView.notifyDatasetChanged();

        // go through each repeatable event
        for( int i=0; i<repeats.size(); ++i) {
            WeekViewEventRepeatable source = repeats.get(i);
            Calendar date = Calendar.getInstance();
            for( int index=0; index<7; ++index) {
                // This is 9/20/15. Calendar is 0-11 while WeekView is 1-12
                date.set(2015, 8, 20);
                if( source.getDay(index) ) {
                    date.add(Calendar.DAY_OF_YEAR, index);
                    for( int count=index; count<84; count+=7) {

                        // set the start and end time of the event
                        Calendar start = Calendar.getInstance();
                        start.set(Calendar.YEAR, date.get(Calendar.YEAR));
                        start.set(Calendar.MONTH, date.get(Calendar.MONTH));
                        start.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                        start.set(Calendar.HOUR_OF_DAY, source.getStartHour());
                        start.set(Calendar.MINUTE, source.getStartMinute());

                        Calendar end = Calendar.getInstance();
                        end.set(Calendar.YEAR, date.get(Calendar.YEAR));
                        end.set(Calendar.MONTH, date.get(Calendar.MONTH));
                        end.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                        end.set(Calendar.HOUR_OF_DAY, source.getEndHour());
                        end.set(Calendar.MINUTE, source.getEndMinute());

                        boolean enabled = true;

                        // check if it matches any of the disabled events
                        for(int j = 0; j < disabled.size(); j++) {
                            if(disabledOverlapping(start, end, source.getRepeatableId(), disabled.get(j))) {
                                System.out.println("Disabling an event");
                                enabled = false;
                                break;
                            }
                        }

                        // add the event with the given enabled state
                        WeekViewEvent newEvent = new WeekViewEvent(Math.abs(rand.nextLong()), source.getName(),
                                source.getBuildingLocation(), source.getLocation(), source.getNote(),
                                source.getRepeatableId(), date.get(Calendar.YEAR), date.get(Calendar.MONTH)+1,
                                date.get(Calendar.DAY_OF_MONTH), source.getStartHour(), source.getStartMinute(),
                                date.get(Calendar.YEAR), date.get(Calendar.MONTH)+1, date.get(Calendar.DAY_OF_MONTH),
                                source.getEndHour(), source.getEndMinute(), enabled);

                        // add to the event list
                        events.add(newEvent);
                        date.add(Calendar.DAY_OF_YEAR, 7);
                    }

                }

            } // days of week loop

        } // repeat arrayList loop

        // update the data set
        mWeekView.notifyDatasetChanged();
    }

    // check whether two events are overlapping
    private boolean areEventsOverlapping(WeekViewEvent event1, WeekViewEvent event2) {
        long start1 = event1.getStartTime().getTimeInMillis();
        long end1 = event1.getEndTime().getTimeInMillis();
        long start2 = event2.getStartTime().getTimeInMillis();
        long end2 = event2.getEndTime().getTimeInMillis();

        return !((start1 >= end2) || (end1 <= start2));
    }

    // check whether for a given time and id, it needs to be disabled
    private boolean disabledOverlapping(Calendar start1, Calendar end1, long repeatableId, DisabledRepeatable disabled) {
        return (start1.get(Calendar.MONTH) == disabled.getStartTime().get(Calendar.MONTH) &&
                start1.get(Calendar.DAY_OF_MONTH) == disabled.getStartTime().get(Calendar.DAY_OF_MONTH) &&
                start1.get(Calendar.HOUR) == disabled.getStartTime().get(Calendar.HOUR) &&
                start1.get(Calendar.MINUTE) == disabled.getStartTime().get(Calendar.MINUTE) &&
                end1.get(Calendar.MONTH) == disabled.getEndTime().get(Calendar.MONTH) &&
                end1.get(Calendar.DAY_OF_MONTH) == disabled.getEndTime().get(Calendar.DAY_OF_MONTH) &&
                end1.get(Calendar.HOUR) == disabled.getEndTime().get(Calendar.HOUR) &&
                end1.get(Calendar.MINUTE) == disabled.getEndTime().get(Calendar.MINUTE) &&
                repeatableId == disabled.getRepeatableId());
    }

    // get the event title
    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    // called when an event is tapped once
    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        if(event.isRepeatable()) {

            // if it is repeatable and enabled, disable it and update the state
            if(event.isEnabled()) {
                DisabledRepeatable newDisabled = new DisabledRepeatable(event.getRepeatableId(),
                        event.getStartTime(), event.getEndTime());
                newDisabled.saveInBackground();
                disabled.add(newDisabled);
            } else {

                // otherwise re-enable it by deleting from the disabled list
                for(int j = 0; j < disabled.size(); j++) {
                    if(disabledOverlapping(event.getStartTime(), event.getEndTime(), event.getRepeatableId(), disabled.get(j))) {
                        disabled.get(j).deleteInBackground();
                        disabled.remove(j);
                        break;
                    }
                }
            }

            // repopulate and update dataset
            populateRepeatable();
            mWeekView.notifyDatasetChanged();

        // for single events change the color and notify the data set
        } else {
            event.changeColor();
            mWeekView.notifyDatasetChanged();
        }
    }

    @Override
    public void onEmptyViewLongPress(Calendar startTime) {
        tappedStart = startTime;
        startActivityForResult(new Intent( this, InputEventActivity.class ), 1 );
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

        // only do something in edit mode
        if (editMode) {

            // if a repeatable event is to be edited
            if(event.isRepeatable()) {

                // set the tappedRepeat to the given tapped event
                for(int i = 0; i < repeats.size(); i++) {
                    if(repeats.get(i).getRepeatableId() == event.getRepeatableId()) {
                        tappedRepeat = repeats.get(i);
                        break;
                    }
                }

                // start the edit activity
                startActivityForResult(new Intent(this, EditRepeatableEventActivity.class), 2);

            // otherwise set the tappedSingle event and start the edit activity
            } else {
                tappedSingle = event;
                startActivityForResult(new Intent(this, EditEventActivity.class), 1);
            }
        }

        // if not in edit mode then view the single event
        else {
            tappedSingle = event;
            startActivity(new Intent(this, ViewEventActivity.class));
        }
    }

    // used to get event from InputEventActivity class
    public static WeekViewEvent getCurrentEvent() { return tappedSingle; }

    // get the tapped repeatable event
    public static WeekViewEventRepeatable getCurrentRepeatableEvent() { return tappedRepeat; }

    public static Calendar getTappedStartTime() {return tappedStart;}
}