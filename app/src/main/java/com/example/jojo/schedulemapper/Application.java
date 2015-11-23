package com.example.jojo.schedulemapper;

/**
 * Created by nathanng on 10/26/15.
 */
import com.alamkanak.weekview.DisabledRepeatable;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ParseACL;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewEventRepeatable;

public class Application extends android.app.Application {

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
        ParseObject.registerSubclass(WeekViewEvent.class);
        ParseObject.registerSubclass(WeekViewEventRepeatable.class);
        ParseObject.registerSubclass(DisabledRepeatable.class);
        Parse.initialize(this, "FKw3bE2VYt6bGQtvX8znzUa59p8EvbfZ9o9fwCJ6", "zxqFBO3QVFZQBU1Onyekp3X7n736wPe2sqdm5p1j");
    }

}
