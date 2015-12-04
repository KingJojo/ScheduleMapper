package com.example.jojo.schedulemapper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.LocationManager;
import android.location.Location;
import android.location.Criteria;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;


import com.alamkanak.weekview.DisabledRepeatable;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewEventRepeatable;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/*
 * Class that creates the map for routing you throughout the day.
 * After pulling and building the map, queries Parse for the list of events and finds the
 * next one that has a start time after the current time. TIes are broken alphabetically
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private String[] locations = {
            "CSE",
            "Center",
            "WLH",
            "Ledden",
            "Price",
            "York",
            "Galbraith",
            "Peterson",
            "Cogs",
            "Sequoyah",
            "APM",
            "Solis"
    };

    private LatLng[] destArray = {
            new LatLng(32.881718, -117.233321),
            new LatLng(32.880945, -117.234413),
            new LatLng(32.878069, -117.237387),
            new LatLng(32.878860, -117.241808),
            new LatLng(32.879766, -117.236943),
            new LatLng(32.874500, -117.240342),
            new LatLng(32.874144, -117.240927),
            new LatLng(32.879931, -117.239885),
            new LatLng(32.880345, -117.239032),
            new LatLng(32.882142, -117.240613),
            new LatLng(32.879013, -117.241084),
            new LatLng(32.880832, -117.239640)
    };

    // the Map
    private GoogleMap mMap;
    private LocationManager locationManager;

    // current position
    private LatLng myPosition;

    // Array lists to store the query results
    private ArrayList<WeekViewEvent> events;
    private ArrayList<WeekViewEventRepeatable> repeats;
    private ArrayList<DisabledRepeatable> disabled;

    // random id for generated events from Repeatable
    Random rand = new Random();

    // current day and day after
    Calendar now;
    Calendar tomorrow;

    // current time
    int dayOfWeek;
    int hour;
    int minute;

    // text bubble icon generator
    private IconGenerator icnGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize dates and times
        now = Calendar.getInstance();

        icnGenerator = new IconGenerator(this);
        dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        hour = now.get(Calendar.HOUR);
        minute = now.get(Calendar.MINUTE);

        tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);

        tomorrow.add(Calendar.DATE, 1);

        // query for events in parse
        events = new ArrayList<WeekViewEvent>();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("WeekViewEvent");

        // search only between today and tomorrow
        query.whereGreaterThanOrEqualTo("startTime", now.getTime());
        query.whereLessThanOrEqualTo("endTime", tomorrow.getTime());
        query.whereEqualTo("enabled", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {

                    // once found, add to the eventList
                    for (int i = 0; i < eventList.size(); i++) {
                        if (((WeekViewEvent) eventList.get(i)).isEnabled()) {
                            events.add((WeekViewEvent) eventList.get(i));
                        }
                    }

                    // next get the repeats
                    getRepeats();
                } else {
                    // handle Parse Exception here
                }
            }
        });

        // ensure strict thread policy
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // set the layout
        setContentView(R.layout.activity_map);

        // get the map and set the location/manager
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // get the current location using our location manager
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                // Getting latitude of the current location
                double latitude = location.getLatitude();

                // Getting longitude of the current location
                double longitude = location.getLongitude();

                // Creating a LatLng object for the current location
                myPosition = new LatLng(latitude, longitude);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 15));
            }
        }
    }

    // method to query for the repeatable events
    private void getRepeats() {
        repeats = new ArrayList<WeekViewEventRepeatable>();

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("WeekViewEventRepeatable");

        // search only for events on the current day of week
        switch(dayOfWeek) {
            case Calendar.SUNDAY:
                query2.whereEqualTo("sunday", true);
                break;
            case Calendar.MONDAY:
                query2.whereEqualTo("monday", true);
                break;
            case Calendar.TUESDAY:
                query2.whereEqualTo("tuesday", true);
                break;
            case Calendar.WEDNESDAY:
                query2.whereEqualTo("wednesday", true);
                break;
            case Calendar.THURSDAY:
                query2.whereEqualTo("thursday", true);
                break;
            case Calendar.FRIDAY:
                query2.whereEqualTo("friday", true);
                break;
            case Calendar.SATURDAY:
                query2.whereEqualTo("saturday", true);
                break;
        }

        // search after the curent hour
        query2.whereGreaterThanOrEqualTo("startHour", hour);
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {

                    // add all events found to the list
                    for (int i = 0; i < eventList.size(); i++) {
                        repeats.add((WeekViewEventRepeatable) eventList.get(i));
                    }

                    // query the disabled events
                    getDisabled();
                } else {
                    // handle Parse Exception here
                }
            }
        });
    }

    // get the disabled events for repeatable
    private void getDisabled() {
        disabled = new ArrayList<DisabledRepeatable>();

        ParseQuery<ParseObject> query3 = new ParseQuery<ParseObject>("DisabledRepeatable");

        // search only after today's current time to tomorrow
        query3.whereGreaterThanOrEqualTo("startTime", now.getTime());
        query3.whereLessThanOrEqualTo("endTime", tomorrow.getTime());
        query3.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < eventList.size(); i++) {
                        disabled.add((DisabledRepeatable) eventList.get(i));
                    }

                    // load the events and choose the latest one
                    fillMap();
                } else {
                    // handle Parse Exception here
                }
            }
        });
    }

    // load the events and chooses the latest one
    private void fillMap() {
        for(int i = 0; i < repeats.size(); i++) {

            // search through each repeat event
            WeekViewEventRepeatable source = repeats.get(i);
            Calendar start = Calendar.getInstance();

            // get start time
            start.set(Calendar.HOUR_OF_DAY, source.getStartHour());
            start.set(Calendar.MINUTE, source.getStartMinute());

            // get end time
            Calendar end = Calendar.getInstance();
            end.set(Calendar.HOUR_OF_DAY, source.getEndHour());
            end.set(Calendar.MINUTE, source.getEndMinute());

            // default enabled
            boolean enabled = true;

            // search through disabled events to see if need to flip boolean
            for(int j = 0; j < disabled.size(); j++) {
                if(disabledOverlapping(start, end, source.getRepeatableId(), disabled.get(j))) {
                    enabled = false;
                    break;
                }
            }

            // if not flipped, add the event to the list
            if(enabled) {
                WeekViewEvent newEvent = new WeekViewEvent(Math.abs(rand.nextLong()), source.getName(), source.getBuildingLocation(),
                        source.getLocation(), source.getNote(), source.getRepeatableId(), now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), source.getStartHour(),
                        source.getStartMinute(), now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1,
                        now.get(Calendar.DAY_OF_MONTH), source.getEndHour(), source.getEndMinute(), true);
                events.add(newEvent);
            }
        }

        // sort based on start time
        Collections.sort(events, new Comparator<WeekViewEvent>() {
            @Override
            public int compare(WeekViewEvent e1, WeekViewEvent e2) {
                return e1.getStartTime().compareTo(e2.getStartTime()); // Ascending
            }
        });


        // if there are events in the list, grab the first one
        if(events.size() != 0) {

            WeekViewEvent firstEvent = events.get(0);

            System.out.println(events.size());
            int counter = 1;
            for(int i = 1; i < events.size(); i++) {
                if(events.get(i).getStartTime().compareTo(firstEvent.getStartTime()) == 0) {
                    counter++;
                }
            }

            if(counter > 1) {

                String[] items = new String[counter];
                for(int i = 0; i < counter; i++) {
                    items[i] = events.get(i).getName() + " at " + events.get(i).getLocation();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose an event");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        routeToEvent(events.get(item));
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                routeToEvent(firstEvent);
            }

        }

        else {
            Toast.makeText(getApplicationContext(), "No more events left today!", Toast.LENGTH_SHORT).show();
        }

    }

    public void routeToEvent(WeekViewEvent event) {

        //the destination to route to
        LatLng destination = null;
        GMapV2Direction md = new GMapV2Direction();
        Document doc;

        for(int i = 0; i < locations.length; i++) {
            if(event.getBuildingLocation().equals(locations[i])) {
                destination = destArray[i];
                break;
            }
        }

        // route to the location
        doc = md.getDocument(myPosition, destination);

        // add a marker to show the event name and time
        SimpleDateFormat fr = new SimpleDateFormat("HH:mm", Locale.US);
        mMap.addMarker(new MarkerOptions().position(destination).visible(true).title("location")
                .icon(BitmapDescriptorFactory.fromBitmap(icnGenerator.makeIcon(event.getName() +
                        " at " + fr.format(event.getStartTime().getTime())))));

        // get the routing polyline
        ArrayList<LatLng> directionPoint = md.getDirection(doc);
        PolylineOptions rectLine = new PolylineOptions().width(15).color(Color.RED);

        float duration = (float) md.getDurationValue(doc)/60f;

        // loop and add each individual line
        for (int k = 0; k < directionPoint.size(); k++) {

            // if at the middle of the route, place a marker showing estimated duration
            if (k == directionPoint.size() / 2) {
                mMap.addMarker(new MarkerOptions().position(directionPoint.get(k)).visible(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(icnGenerator.makeIcon("ETA: " +
                                String.format("%.1f", duration) + " min"))));
            }
            rectLine.add(directionPoint.get(k));
        }

        mMap.addPolyline(rectLine);
    }

    // checks whether the disabled event matches with the given time and id
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
