package com.example.jojo.schedulemapper;

/**
 * Created by nathanng on 10/26/15.
 */
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ParseACL;

public class Application extends android.app.Application {

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(getApplicationContext());
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "FKw3bE2VYt6bGQtvX8znzUa59p8EvbfZ9o9fwCJ6", "zxqFBO3QVFZQBU1Onyekp3X7n736wPe2sqdm5p1j");
        ParseObject.registerSubclass(Event.class);
    }

}
