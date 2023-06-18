package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * fragment to google maps that show all super markets and my current location
 */
public class MapsFragment extends Fragment {
    //properties
    private final ArrayList<SuperMarket> arrayList;
    private LatLng myPosition;

    /**
     * constructor
     */
    public MapsFragment() {
        SuperMarket superMarket = new SuperMarket();
        arrayList = superMarket.getAllSuperMarkets();
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         * @param googleMap - GoogleMap
         */
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            //zoom level. 1- world, 5-continent, 10-city, 15-streets, 20-buildings:

            //set default camera position to my current location
            googleMap.setMinZoomPreference(14);
            googleMap.setMaxZoomPreference(18);
            if (myPosition != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));

            //get context and add button to focus on my current location
            if (ActivityCompat.checkSelfPermission(requireContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
            // Instantiates a new CircleOptions object and defines the center and radius In meters
            CircleOptions circleOptions;
            if (myPosition != null) {
                circleOptions = new CircleOptions().center(myPosition).radius(20);
                // add the circle to the map:
                Circle circle = googleMap.addCircle(circleOptions);
                circle.setStrokeWidth(20);
                circle.setStrokeColor(Color.BLACK);
                circle.setStrokeWidth(2);
                circle.setFillColor(Color.BLUE);
            }

            UiSettings uiSetting = googleMap.getUiSettings();
            uiSetting.setZoomControlsEnabled(true);


            //add marker to all locations
            for(int i = 0; i < arrayList.size(); i++)
            {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(arrayList.get(i).getIcon());           //icon of marker
                MarkerOptions markerOptions = new MarkerOptions().position(arrayList.get(i).getLocation()).title(arrayList.get(i).getName());
                markerOptions.title(arrayList.get(i).getName());
                markerOptions.icon(icon);       //optional
                googleMap.addMarker(markerOptions);  //add marker to the map
            }
        }
    };

    /**
     * called when the on fragment's view is created
     * @param inflater - inflate the layout for the fragment
     * @param container - the view that returned view will be added to him
     * @param savedInstanceState - Bundle saved instance of the fragment
     * @return - return the fragment view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    /**
     * called when fragment has been created, and the fragment's view has been initialized
     * @param view - View
     * @param savedInstanceState - Bundle saved instance of the fragment
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    /**
     * set my current location
     * @param longitude - current longitude
     * @param latitude - current latitude
     */
    public void setMyLocation(double longitude, double latitude)
    {
        myPosition = new LatLng(longitude, latitude);
    }
}