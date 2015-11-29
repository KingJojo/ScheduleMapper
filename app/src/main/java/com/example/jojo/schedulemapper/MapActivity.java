package com.example.jojo.schedulemapper;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.LocationManager;
import android.location.Location;
import android.location.Criteria;
import android.support.v4.content.ContextCompat;


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

/* unused import statements

import java.util.Date;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.SupportMapFragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.content.Intent;

*/

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LatLng myPosition;
    private ArrayList<WeekViewEvent> events;
    private ArrayList<WeekViewEventRepeatable> repeats;
    private ArrayList<DisabledRepeatable> disabled;
    Random rand = new Random();
    Calendar now;
    Calendar tomorrow;
    int dayOfWeek;
    int hour;
    int minute;
    private IconGenerator icnGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        events = new ArrayList<WeekViewEvent>();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("WeekViewEvent");
        query.whereGreaterThanOrEqualTo("startTime", now.getTime());
        query.whereLessThanOrEqualTo("endTime", tomorrow.getTime());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {
                    System.out.println("found " + eventList.size());
                    for (int i = 0; i < eventList.size(); i++) {
                        System.out.println("name: " + ((WeekViewEvent) eventList.get(i)).getName());
                        if (((WeekViewEvent) eventList.get(i)).isEnabled()) {
                            events.add((WeekViewEvent) eventList.get(i));
                        }
                    }
                    getRepeats();
                } else {
                    // handle Parse Exception here
                }
            }
        });




        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_map);
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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

    private void getRepeats() {
        repeats = new ArrayList<WeekViewEventRepeatable>();

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("WeekViewEventRepeatable");
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
        System.out.println(dayOfWeek + ", " + hour + ", " + minute);
        query2.whereGreaterThanOrEqualTo("startHour", hour);
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {
                    System.out.println("found " + eventList.size());
                    for (int i = 0; i < eventList.size(); i++) {
                        System.out.println("name: " + ((WeekViewEventRepeatable) eventList.get(i)).getName());
                        repeats.add((WeekViewEventRepeatable) eventList.get(i));
                    }
                    getDisabled();
                } else {
                    // handle Parse Exception here
                }
            }
        });
    }

    private void getDisabled() {
        disabled = new ArrayList<DisabledRepeatable>();

        ParseQuery<ParseObject> query3 = new ParseQuery<ParseObject>("DisabledRepeatable");
        query3.whereGreaterThanOrEqualTo("startTime", now.getTime());
        query3.whereLessThanOrEqualTo("endTime", tomorrow.getTime());
        query3.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {
                    System.out.println("found " + eventList.size());
                    for (int i = 0; i < eventList.size(); i++) {
                        disabled.add((DisabledRepeatable) eventList.get(i));
                    }
                    fillMap();
                } else {
                    // handle Parse Exception here
                }
            }
        });
    }

    private void fillMap() {
        for(int i = 0; i < repeats.size(); i++) {
            WeekViewEventRepeatable source = repeats.get(i);
            Calendar start = Calendar.getInstance();
            start.set(Calendar.HOUR_OF_DAY, source.getStartHour());
            start.set(Calendar.MINUTE, source.getStartMinute());

            Calendar end = Calendar.getInstance();
            end.set(Calendar.HOUR_OF_DAY, source.getEndHour());
            end.set(Calendar.MINUTE, source.getEndMinute());

            boolean enabled = true;

            for(int j = 0; j < disabled.size(); j++) {
                if(disabledOverlapping(start, end, source.getRepeatableId(), disabled.get(j))) {
                    enabled = false;
                    break;
                }
            }

            if(enabled) {
                WeekViewEvent newEvent = new WeekViewEvent(Math.abs(rand.nextLong()), source.getName(), source.getBuildingLocation(),
                        source.getLocation(), source.getNote(), source.getRepeatableId(), now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), source.getStartHour(),
                        source.getStartMinute(), now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1,
                        now.get(Calendar.DAY_OF_MONTH), source.getEndHour(), source.getEndMinute(), true);
                events.add(newEvent);
            }
        }

        Collections.sort(events, new Comparator<WeekViewEvent>() {
            @Override
            public int compare(WeekViewEvent e1, WeekViewEvent e2) {
                return e1.getStartTime().compareTo(e2.getStartTime()); // Ascending
            }
        });

        LatLng cse = new LatLng(32.881718, -117.233321);
        LatLng wlh = new LatLng(32.880945, -117.234413);
        LatLng center = new LatLng(32.878069, -117.237387);
        LatLng ledden = new LatLng(32.878860, -117.241808);
        LatLng price = new LatLng(32.879766, -117.236943);
        LatLng york = new LatLng(32.874500, -117.240342);
        LatLng galbraith = new LatLng(32.874144, -117.240927);
        LatLng peterson = new LatLng(32.879931, -117.239885);
        LatLng cogs = new LatLng(32.880345, -117.239032);
        LatLng sequoyah = new LatLng(32.882142, -117.240613);
        LatLng apm = new LatLng(32.879013, -117.241084);
        LatLng solis = new LatLng(32.880832, -117.239640);
        LatLng destination;

        if(events.size() != 0) {
            WeekViewEvent event = events.get(0);
            System.out.println(event.getLocation());
            System.out.println(event.getBuildingLocation());
            System.out.println(event.getBuildingNumber());
            GMapV2Direction md = new GMapV2Direction();
            Document doc;
            if (event.getBuildingLocation().equals("CSE"))
                destination = cse;
            else if (event.getBuildingLocation().equals("Center"))
                destination = center;
            else if (event.getBuildingLocation().equals("WLH"))
                destination = wlh;
            else if (event.getBuildingLocation().equals("Ledden"))
                destination = ledden;
            else if (event.getBuildingLocation().equals("Price"))
                destination = price;
            else if (event.getBuildingLocation().equals("York"))
                destination = york;
            else if (event.getBuildingLocation().equals("Galbraith"))
                destination = galbraith;
            else if (event.getBuildingLocation().equals("Peterson"))
                destination = peterson;
            else if (event.getBuildingLocation().equals("Cogs"))
                destination = cogs;
            else if (event.getBuildingLocation().equals("Sequoyah"))
                destination = sequoyah;
            else if (event.getBuildingLocation().equals("APM"))
                destination = apm;
            else
                destination = solis;

            doc = md.getDocument(myPosition, destination);

            SimpleDateFormat fr = new SimpleDateFormat("HH:mm", Locale.US);
            mMap.addMarker(new MarkerOptions().position(destination).visible(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(icnGenerator.makeIcon(event.getName() + " at " + fr.format(event.getStartTime().getTime())))));
            
            ArrayList<LatLng> directionPoint = md.getDirection(doc);
            PolylineOptions rectLine = new PolylineOptions().width(15).color(
                    Color.RED);

            for (int k = 0; k < directionPoint.size(); k++) {
                if(k == directionPoint.size()/2) {
                    mMap.addMarker(new MarkerOptions().position(directionPoint.get(k)).visible(true)
                            .icon(BitmapDescriptorFactory.fromBitmap(icnGenerator.makeIcon("ETA: " + md.getDurationText(doc)))));

                }
                rectLine.add(directionPoint.get(k));
            }

            mMap.addPolyline(rectLine);
        }

    }

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
