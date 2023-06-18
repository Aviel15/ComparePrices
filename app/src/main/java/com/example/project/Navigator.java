package com.example.project;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * convert latitude and longitude to address, make easier searching about supermarket
 */
public class Navigator {
    //properties
    private final Geocoder geocoder;                          //geocoder
    private final String address;                             //address to which to navigate
    private double latitude, longitude;                 //latitude and longitude

    /**
     * constructor
     * @param context - context
     * @param address - address name
     */
    public Navigator(Context context, String address) {
        //initializes address, context and geocoder
        this.address = address;
        geocoder = new Geocoder(context);
        //creates, runs and joins the navigator thread
        NavigatorThread navThread = new NavigatorThread();
        navThread.start();
        try {
            navThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //inner class NavigatorThread extends from Thread
    private class NavigatorThread extends Thread {
        /**
         * called when the thread start to run
         */
        public void run() {
            try {
                List<Address> addressList = geocoder.getFromLocationName(address, 1);       //convert from address to latitude and longitude
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    latitude = address.getLatitude();
                    longitude = address.getLongitude();
                }
            } catch (IOException e) {
                Log.d("tagCHECK", "Unable to connect to Geocoder", e);
            }
        }
    }

    /**
     * return the latitude
     * @return - latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * return the longitude
     * @return - longitude
     */
    public double getLongitude() {
        return longitude;
    }
}
