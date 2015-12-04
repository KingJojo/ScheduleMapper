package com.example.jojo.schedulemapper;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

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
        super(ScheduleActivity.class);
    }

    public void testRun() {
        solo.clickOnView(solo.getView(R.id.action_add_event));
        solo.waitForActivity(InputEventActivity.class);
        solo.clickOnView(solo.getView(R.id.repeatableYes));
        solo.enterText(0, "CSE100 Lecture");
        solo.clickOnView(solo.getView(R.id.buildingLocation));
        solo.scrollToTop();
        solo.clickOnView(solo.getView(TextView.class, 1));
        solo.enterText(1, "103");
        solo.enterText(2, "Don't be late!");
        solo.clickOnView(solo.getView(R.id.startButton));
        solo.setTimePicker(0, 5, 0);
        solo.clickOnButton("OK");
        solo.clickOnView(solo.getView(R.id.endButton));
        solo.setTimePicker(0, 6, 20);
        solo.clickOnButton("OK");
        solo.clickOnView(solo.getView(R.id.checkBoxMon));
        solo.clickOnView(solo.getView(R.id.checkBoxWed));
        solo.clickOnView(solo.getView(R.id.checkBoxFri));
        solo.clickOnView(solo.getView(R.id.submit));
        solo.waitForActivity(ScheduleActivity.class);
        solo.clickOnView(solo.getView(R.id.weekView));
        solo.clickLongOnScreen(631.5f, 1366.1f, 2000);
        solo.waitForActivity(ViewEventActivity.class);
        String name = solo.getText(0).getText().toString();
        String location = solo.getText(1).getText().toString();
        String date = solo.getText(3).getText().toString();
        String start = solo.getText(5).getText().toString();
        String end = solo.getText(7).getText().toString();
        String note = solo.getText(8).getText().toString();

        assertEquals("CSE100 Lecture", name);
        assertEquals("Center 103", location);
        assertEquals("11/30/2015", date);
        assertEquals( "5:00", start);
        assertEquals("6:20", end);
        assertEquals("Don't be late!", note);
    }
}