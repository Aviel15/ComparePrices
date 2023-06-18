package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * activity to show the map, show map and button to add a supermarket
 */
public class MapShowActivity extends AppCompatActivity {
    //properties
    private final MapsFragment mapsFragment = new MapsFragment();             //object of type MapsFragment
    private final static int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;            //id to locations permission
    private final static int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 101;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_show);

        //get permission
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        }
        else { //if already have permissions:
            initLocationListener();
        }

        //create the fragment to show the google map with my location and supermarket's location
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map,mapsFragment);
        fragmentTransaction.commit();
    }

    /**
     * create option menu
     * @param menu - Menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * check if item selected, if yes will go back to menu
     * @param item - MenuItem
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item) {
            Intent i = new Intent(this, MainActivity.class);             //move to comparison of specific product
            startActivity(i);
        }
        return true;
    }

    /**
     * add supermarket will move add super activity and add super market to map
     * @param view - the button was clicked
     */
    public void onClick(View view) {
        Intent i = new Intent(this, AddSuperActivity.class);            //move to AddSuperActivity
        startActivity(i);
    }

    /**
     * update the current location to know which show super markets are the closest to me
     */
    public void initLocationListener(){
        //create new location listener object, with min time of 3 seconds and min distance of 20m:
        try {
            Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            double currentLatitude = myLocation.getLatitude();
            double currentLongitude = myLocation.getLongitude();
            mapsFragment.setMyLocation(currentLatitude, currentLongitude);                          //set to fragment the current location
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}