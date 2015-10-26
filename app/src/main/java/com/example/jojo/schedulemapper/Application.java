package com.example.jojo.schedulemapper;

/**
 * Created by nathanng on 10/26/15.
 */
import com.parse.Parse;
import com.parse.ParseObject;

public class Application extends android.app.Application {

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Event.class);
        Parse.initialize(this, "FKw3bE2VYt6bGQtvX8znzUa59p8EvbfZ9o9fwCJ6", "zxqFBO3QVFZQBU1Onyekp3X7n736wPe2sqdm5p1j");
    }

}
