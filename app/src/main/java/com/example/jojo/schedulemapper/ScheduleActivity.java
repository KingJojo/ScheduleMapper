package com.example.jojo.schedulemapper;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.content.Intent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Button;
import android.graphics.Color;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    private List<WeekViewEvent> events;
    private static WeekViewEvent tapped = null;
    private boolean editMode = false; // used to toggle between edit and view mode
    private int id = 0;
    private int[] colorArray = {Color.parseColor("#59dbe0"), Color.parseColor("#f57f68"),
                                        Color.parseColor("#87d288"), Color.parseColor("#f8b552")};
    private int colorIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedule);

        events = new ArrayList<WeekViewEvent>();

        // Sample events specifically for lab day
        WeekViewEvent newEvent1 = new WeekViewEvent(id, "5:30 Morning Jog", null, null,
                2015, 11, 6, 5, 30, 2015, 11, 6, 6, 30);
        id++;
        WeekViewEvent newEvent2 = new WeekViewEvent(id, "CSE 110 Lab", null, null,
                2015, 11, 6, 10, 30, 2015, 11, 6, 12, 30);
        id++;
        events.add(newEvent1);
        events.add(newEvent2);
        System.out.println(events.size());

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
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if(tapped != null) {
                    events.remove(tapped);
                }
                id++;
                int year = data.getIntExtra("year", 0);
                int month = data.getIntExtra("month", 0);
                int day = data.getIntExtra("day", 0);
                boolean[] daysOfWeek = data.getBooleanArrayExtra("daysOfWeek");
                int firstDay = -1;
                for (int i=0; i<daysOfWeek.length; i++) {
                    if (daysOfWeek[i]) {
                        if (firstDay == -1) firstDay = i;
                        System.out.println("Making new event for day" + (day+i-firstDay));
                        WeekViewEvent newEvent = new WeekViewEvent(id, data.getStringExtra("eventTitle"),
                                data.getStringExtra("location"), data.getStringExtra("note"),
                                year, month, day+i-firstDay, data.getIntExtra("startHour", 0),
                                data.getIntExtra("startMinute", 0), year, month, day+i-firstDay,
                                data.getIntExtra("endHour", 0), data.getIntExtra("endMinute", 0));
                        newEvent.setColor(colorArray[colorIndex]);
                        id++;
                        events.add(newEvent);
                        mWeekView.notifyDatasetChanged();
                    }
                }
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
                // change edit button appearance to show edit mode
                if (editMode) {
                    editMode = false;
                    item.setTitle("Edit");
                }
                else {
                    editMode = true;
                    item.setTitle("View");
                }
                return true;
            case R.id.action_add_event:
                tapped = null;
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
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> weekviewEvents = new ArrayList<WeekViewEvent>();
        for(WeekViewEvent event: events){
            if(event.getStartTime().get(Calendar.MONTH) == newMonth &&
                    event.getStartTime().get(Calendar.YEAR) == newYear)
            {
                weekviewEvents.add(event);
            }
        }
        return weekviewEvents;

    }

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        event.changeColor();
        mWeekView.notifyDatasetChanged();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        if (editMode) {
            tapped = event;
            startActivityForResult(new Intent(this, InputEventActivity.class), 1);
        }
    }

    // used to get event from inputeventactivity class
    public static WeekViewEvent getCurrentEvent() {
        return tapped;
    }
}