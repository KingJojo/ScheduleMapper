package com.example.jojo.schedulemapper;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
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
 */
public class ScheduleActivity extends AppCompatActivity implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener {

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private static final String TAG = "PROFILE";
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    private MenuItem viewMenu;
    private MenuItem editMenu;
    private List<WeekViewEvent> events;
    private List<WeekViewEventRepeatable> repeats;
    private static WeekViewEvent tappedSingle = null;
    private static WeekViewEventRepeatable tappedRepeat = null;
    private boolean editMode = false; // used to toggle between edit and view mode
    private int id = 0;
    private int[] colorArray = {Color.parseColor("#59dbe0"), Color.parseColor("#f57f68"),
                                        Color.parseColor("#87d288"), Color.parseColor("#f8b552")};
    private int colorIndex = 0;
    Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedule);

        events = new ArrayList<WeekViewEvent>();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("WeekViewEvent");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {
                    System.out.println("found " + eventList.size());
                    for (int i = 0; i < eventList.size(); i++) {
                        System.out.println("name: " + ((WeekViewEvent) eventList.get(i)).getName());
                        events.add((WeekViewEvent) eventList.get(i));
                    }
                    mWeekView.notifyDatasetChanged();
                } else {
                    // handle Parse Exception here
                }
            }
        });

        //Log.v("Profile", "Event size: " + events.size());

        repeats = new ArrayList<WeekViewEventRepeatable>();

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("WeekViewEventRepeatable");
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {
                    System.out.println("found " + eventList.size());
                    for (int i = 0; i < eventList.size(); i++) {
                        System.out.println("name: " + ((WeekViewEventRepeatable) eventList.get(i)).getName());
                        repeats.add((WeekViewEventRepeatable) eventList.get(i));
                    }
                    mWeekView.notifyDatasetChanged();
                    populateRepeatable();

                } else {
                    // handle Parse Exception here
                }
            }
        });
        //Log.v("Profile", "repeats size: " + repeats.size());

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        mWeekView.setNumberOfVisibleDays(3);

        // Lets change some dimensions to best fit the view.
        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));

        mWeekView.notifyDatasetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        this.viewMenu = menu.findItem(R.id.change_view);
        this.editMenu = menu.findItem(R.id.edit_menu);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if(data == null) {
                    if (tappedSingle != null) {
                        tappedSingle.deleteInBackground();
                        events.remove(tappedSingle);
                        mWeekView.notifyDatasetChanged();
                        return;
                    }
                }

                if(tappedSingle != null) {
                    events.remove(tappedSingle);
                    tappedSingle.deleteInBackground();
                }

                String title = data.getStringExtra("eventTitle");
                String buildingLocation = data.getStringExtra("locationBuilding");
                String location = data.getStringExtra("location");
                String note = data.getStringExtra("note");

                boolean repeatable = data.getBooleanExtra("repeatable", false);

                int startHour = data.getIntExtra("startHour", 0);
                int startMinute = data.getIntExtra("startMinute", 0);
                int endHour = data.getIntExtra("endHour", 0);
                int endMinute = data.getIntExtra("endMinute", 0);

                if( repeatable ) {
                    boolean days[] = data.getBooleanArrayExtra("days");
                    String quarter = data.getStringExtra("quarter");

                    WeekViewEventRepeatable newRepeatable = new WeekViewEventRepeatable(title,
                            buildingLocation, location, note, Math.abs(rand.nextLong()), startHour, startMinute, endHour,
                            endMinute, days, quarter);

                    newRepeatable.setColor(colorArray[colorIndex]);
                    repeats.add(newRepeatable);
                    newRepeatable.saveInBackground();
                    populateRepeatable();
                }

                else {
                    int year = data.getIntExtra("year", 0);
                    int month = data.getIntExtra("month", 0);
                    int day = data.getIntExtra("day", 0);

                    WeekViewEvent newEvent = new WeekViewEvent(Math.abs(rand.nextLong()), title, buildingLocation, location,
                            note, -1, year, month, day, startHour, startMinute, year, month,
                            day, endHour, endMinute);

                    newEvent.setColor(colorArray[colorIndex]);
                    events.add(newEvent);
                    newEvent.saveInBackground();

                    for(int i=0; i<events.size(); ++i){
                        if(areEventsOverlapping(newEvent, events.get(i))){
                            Toast.makeText(getApplicationContext(), "Warning: New event overlaps with existing event.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }

                }

                mWeekView.notifyDatasetChanged();
                colorIndex = (colorIndex + 1) % 4;
            }
        }
        if(requestCode == 2) {
            if (resultCode == RESULT_OK) {
                if(data == null) {
                    if (tappedRepeat != null) {
                        tappedRepeat.deleteInBackground();
                        repeats.remove(tappedRepeat);
                        populateRepeatable();
                        mWeekView.notifyDatasetChanged();
                        return;
                    }
                }

                if(tappedRepeat != null) {
                    repeats.remove(tappedRepeat);
                    tappedRepeat.deleteInBackground();
                }

                String title = data.getStringExtra("eventTitle");
                String buildingLocation = data.getStringExtra("buildingLocation");
                String location = data.getStringExtra("location");
                String note = data.getStringExtra("note");

                int startHour = data.getIntExtra("startHour", 0);
                int startMinute = data.getIntExtra("startMinute", 0);
                int endHour = data.getIntExtra("endHour", 0);
                int endMinute = data.getIntExtra("endMinute", 0);

                boolean days[] = data.getBooleanArrayExtra("days");
                String quarter = data.getStringExtra("quarter");

                WeekViewEventRepeatable newRepeatable = new WeekViewEventRepeatable(title,
                        buildingLocation, location, note, Math.abs(rand.nextLong()), startHour, startMinute, endHour,
                        endMinute, days, quarter);
                newRepeatable.setColor(colorArray[colorIndex]);
                repeats.add(newRepeatable);
                newRepeatable.saveInBackground();
                populateRepeatable();

                mWeekView.notifyDatasetChanged();
                colorIndex = (colorIndex + 1) % 4;

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            case R.id.edit_button:
                editMode = true;
                editMenu.setTitle("Edit");
                return true;
            case R.id.view_button:
                editMode = false;
                editMenu.setTitle("View");
                return true;
            case R.id.action_add_event:
                tappedSingle = null;
                startActivityForResult(new Intent( this, InputEventActivity.class ), 1 );
                return true;
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_next_week:
                mWeekView.incrementWeek();
                return true;
            case R.id.action_prev_week:
                mWeekView.decrementWeek();
                return true;
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
                return hour > 12 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        return events;
    }

    private void populateRepeatable(){

        for (int i = events.size() - 1; i >= 0; --i) {
            if (events.get(i).isRepeatable())
                events.remove(i);
        }

        mWeekView.notifyDatasetChanged();

        for( int i=0; i<repeats.size(); ++i) {
            WeekViewEventRepeatable source = repeats.get(i);
            Calendar date = Calendar.getInstance();
            for( int index=0; index<7; ++index) {
                // This is 9/20/15. Calendar is 0-11 while WeekView is 1-12
                date.set(2015, 8, 20);
                if( source.getDay(index) ) {
                    date.add(Calendar.DAY_OF_YEAR, index);
                    for( int count=index; count<84; count+=7) {
                        //Log.v("PROFILE", "Date: " + (date.get(Calendar.MONTH)+1) + "/" + date.get(Calendar.DAY_OF_MONTH));
                        WeekViewEvent newEvent = new WeekViewEvent(Math.abs(rand.nextLong()), source.getName(), source.getBuildingLocation(),
                                source.getLocation(), source.getNote(), source.getRepeatableId(), date.get(Calendar.YEAR),
                                date.get(Calendar.MONTH)+1, date.get(Calendar.DAY_OF_MONTH), source.getStartHour(),
                                source.getStartMinute(), date.get(Calendar.YEAR), date.get(Calendar.MONTH)+1,
                                date.get(Calendar.DAY_OF_MONTH), source.getEndHour(), source.getEndMinute());
                        events.add(newEvent);
                        date.add(Calendar.DAY_OF_YEAR, 7);
                    }

                }

            } // days of week loop

        } // repeat arrayList loop

        mWeekView.notifyDatasetChanged();
    }

    private boolean areEventsOverlapping(WeekViewEvent event1, WeekViewEvent event2) {
        long start1 = event1.getStartTime().getTimeInMillis();
        long end1 = event1.getEndTime().getTimeInMillis();
        long start2 = event2.getStartTime().getTimeInMillis();
        long end2 = event2.getEndTime().getTimeInMillis();
        return !((start1 >= end2) || (end1 <= start2));
    }
    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        event.changeColor();
        event.saveInBackground();
        mWeekView.notifyDatasetChanged();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        if (editMode) {
            if(event.isRepeatable()) {
                System.out.println(event.getRepeatableId());
                for(int i = 0; i < repeats.size(); i++) {
                    System.out.println(repeats.get(i).getRepeatableId());
                    if(repeats.get(i).getRepeatableId() == event.getRepeatableId()) {
                        tappedRepeat = repeats.get(i);
                        break;
                    }
                }
                System.out.println(tappedRepeat.getRepeatableId());
                startActivityForResult(new Intent(this, EditRepeatableEventActivity.class), 2);
            } else {
                tappedSingle = event;
                startActivityForResult(new Intent(this, EditEventActivity.class), 1);
            }
        }
        else {
            tappedSingle = event;
            startActivity(new Intent(this, ViewEventActivity.class));
        }
    }

    // used to get event from inputeventactivity class
    public static WeekViewEvent getCurrentEvent() {
        return tappedSingle;
    }

    public static WeekViewEventRepeatable getCurrentRepeatableEvent() { return tappedRepeat; }
}