package com.example.jojo.schedulemapper;

import android.graphics.PointF;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.robotium.solo.Solo;

import junit.framework.TestCase;


/**
 * Created by nathanng on 12/4/15.
 */
public class ScheduleActivityTest extends ActivityInstrumentationTestCase2 {
    private Solo solo;

    @Override
    public void setUp() throws Exception {
        //setUp() is run before a test case is started.
        //This is where the solo object is created.
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

    public ScheduleActivityTest() throws ClassNotFoundException {
        super(MainActivity.class);
    }

    public void testRun() {
        /*
         * Scenario 1:
         * Given an empty schedule, when a student adds his lectures and discussions using the prompt,
         * then they should appear in the correct time slots every week on the correct days until
         * the quarter ends.
         */

        /*
         * Given an empty schedule
         */

        solo.clickOnView(solo.getView(R.id.scheduleButton));

        /*
         * When a student adds his lectures and discussions using the prompt
         */

        // add CSE110 Lecture
        solo.clickOnView(solo.getView(R.id.action_add_event));
        solo.waitForActivity(InputEventActivity.class);
        solo.clickOnView(solo.getView(R.id.repeatableYes));
        solo.enterText(0, "CSE100 Lecture");
        solo.clickOnView(solo.getView(R.id.buildingLocation));
        solo.scrollToTop();
        solo.clickOnView(solo.getView(TextView.class, 1));
        solo.enterText(1, "113");
        solo.enterText(2, "Don't be late!");
        solo.clickOnView(solo.getView(R.id.startButton));
        solo.setTimePicker(0, 17, 0);
        solo.clickOnButton("OK");
        solo.clickOnView(solo.getView(R.id.endButton));
        solo.setTimePicker(0, 18, 20);
        solo.clickOnButton("OK");
        solo.clickOnView(solo.getView(R.id.checkBoxMon));
        solo.clickOnView(solo.getView(R.id.checkBoxWed));
        solo.clickOnView(solo.getView(R.id.checkBoxFri));
        solo.clickOnView(solo.getView(R.id.submit));
        solo.waitForActivity(ScheduleActivity.class);

        // add Math180A Discussion
        solo.clickOnView(solo.getView(R.id.action_add_event));
        solo.waitForActivity(InputEventActivity.class);
        solo.clickOnView(solo.getView(R.id.repeatableYes));
        solo.enterText(0, "Math180A Discussion");
        solo.clickOnView(solo.getView(R.id.buildingLocation));
        solo.scrollToTop();
        solo.clickOnView(solo.getView(TextView.class, 10));
        solo.enterText(1, "B412");
        solo.enterText(2, "Remember HW");
        solo.clickOnView(solo.getView(R.id.startButton));
        solo.setTimePicker(0, 19, 0);
        solo.clickOnButton("OK");
        solo.clickOnView(solo.getView(R.id.endButton));
        solo.setTimePicker(0, 20, 0);
        solo.clickOnButton("OK");
        solo.clickOnView(solo.getView(R.id.checkBoxWed));
        solo.clickOnView(solo.getView(R.id.submit));
        solo.waitForActivity(ScheduleActivity.class);

        solo.clickOnView(solo.getView(R.id.weekView));
        solo.drag(591.0f, 591.01f, 1704.1f, 401.2f, 10);
        solo.drag(591.0f, 591.01f, 1704.1f, 401.2f, 10);
        solo.clickLongOnScreen(609.5f, 648.1f, 2000);
        solo.waitForActivity(ViewEventActivity.class);

        String name = solo.getText(0).getText().toString();
        String location = solo.getText(1).getText().toString();
        String date = solo.getText(3).getText().toString();
        String start = solo.getText(5).getText().toString();
        String end = solo.getText(7).getText().toString();
        String note = solo.getText(8).getText().toString();

        assertEquals("CSE100 Lecture", name);
        assertEquals("Center 113", location);
        assertEquals("11/30/2015", date);
        assertEquals( "5:00", start);
        assertEquals("6:20", end);
        assertEquals("Don't be late!", note);

        solo.goBack();
        solo.waitForActivity(ScheduleActivity.class);

        solo.clickOnView(solo.getView(R.id.weekView));
        solo.drag(1049.0f, 188.6f, 988.1f, 988.2f, 10);
        solo.clickLongOnScreen(630.5f, 987.1f, 2000);
        solo.waitForActivity(ViewEventActivity.class);

        String name1 = solo.getText(0).getText().toString();
        String location1 = solo.getText(1).getText().toString();
        String date1 = solo.getText(3).getText().toString();
        String start1 = solo.getText(5).getText().toString();
        String end1 = solo.getText(7).getText().toString();
        String note1 = solo.getText(8).getText().toString();

        assertEquals("Math180A Discussion", name1);
        assertEquals("APM B412", location1);
        assertEquals("12/2/2015", date1);
        assertEquals("7:00", start1);
        assertEquals("8:00", end1);
        assertEquals("Remember HW", note1);

        solo.goBack();
        solo.waitForActivity(ScheduleActivity.class);
        solo.goBack();
        solo.waitForActivity(MainActivity.class);

        solo.clickOnView(solo.getView(R.id.mapButton));
        solo.waitForActivity(MapActivity.class);

    }
}