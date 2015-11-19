package com.example.jojo.schedulemapper;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.LocationManager;
import android.location.Location;
import android.location.Criteria;
import android.content.Context;
import android.support.v4.content.ContextCompat;


import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LatLng myPosition;
    private ArrayList<WeekViewEvent> events;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        events = new ArrayList<WeekViewEvent>();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("WeekViewEvent");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {
                    System.out.println("found " + eventList.size());
                    for (int i = 0; i < eventList.size(); i++) {
                        System.out.println("name: " + ((WeekViewEvent) eventList.get(i)).getName());
                        events.add((WeekViewEvent) eventList.get(i));
                        LatLng cse = new LatLng(32.881718, -117.233321);
                        LatLng wlh = new LatLng(32.880945, -117.234413);
                        LatLng center = new LatLng(32.878069, -117.237387);
                        int lineWidth = 10;
                        LatLng prevDest = myPosition;
                        System.out.println(events.size());
                        for(int j = 0; j < events.size(); j++) {
                            WeekViewEvent event = events.get(j);
                            System.out.println(event.getLocation());
                            System.out.println(event.getBuildingLocation());
                            System.out.println(event.getBuildingNumber());
                            GMapV2Direction md = new GMapV2Direction();
                            Document doc;
                            if(event.getBuildingLocation().equals("CSE")) {
                                doc = md.getDocument(prevDest, cse);
                                prevDest = cse;
                            } else if(event.getBuildingLocation().equals("Center")) {
                                doc = md.getDocument(prevDest, center);
                                prevDest = center;
                            } else {
                                doc = md.getDocument(prevDest, wlh);
                                prevDest = wlh;
                            }

                            ArrayList<LatLng> directionPoint = md.getDirection(doc);
                            PolylineOptions rectLine = new PolylineOptions().width(lineWidth).color(
                                    Color.RED);

                            for (int k = 0; k < directionPoint.size(); k++) {
                                rectLine.add(directionPoint.get(k));
                            }
                            mMap.addPolyline(rectLine);
                            lineWidth = 5;
                        }
                    }
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

        LatLng cse = new LatLng(32.881718, -117.233321);
        LatLng wlh = new LatLng(32.880945, -117.234413);
        LatLng center = new LatLng(32.878069, -117.237387);
        Marker cseMarker = mMap.addMarker(new MarkerOptions()
                .position(cse)
                .draggable(true));
        Marker wlhMarker = mMap.addMarker(new MarkerOptions()
                .position(wlh)
                .draggable(true));
        Marker centerMarker = mMap.addMarker(new MarkerOptions()
                .position(center)
                .draggable(true));

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
