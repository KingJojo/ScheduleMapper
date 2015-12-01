package com.example.jojo.schedulemapper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View.OnClickListener mapListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps");
                Intent i = new Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri);
                i.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                i.setPackage("com.google.android.apps.maps");
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                }
            }
        };

        // set listener for map button
        Button mapBtn = (Button) findViewById(R.id.mapButton);
        mapBtn.setOnClickListener(mapListener);

        View.OnClickListener scheduleListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
            }
        };

        // set listener for schedule button
        Button scheduleBtn = (Button) findViewById(R.id.scheduleButton);
        scheduleBtn.setOnClickListener(scheduleListener);

        /* Set fonts correctly */
        CustomFontHelper changeTitleFont = new CustomFontHelper();
        changeTitleFont.setCustomFont((TextView) findViewById(R.id.textView4), "fonts/Roboto-Thin.ttf", getApplicationContext());
        changeTitleFont.setCustomFont((TextView) findViewById(R.id.mapButton), "fonts/Roboto-Light.ttf", getApplicationContext());
        changeTitleFont.setCustomFont((TextView) findViewById(R.id.scheduleButton), "fonts/Roboto-Light.ttf", getApplicationContext());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
