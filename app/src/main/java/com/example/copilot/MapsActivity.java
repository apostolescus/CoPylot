package com.example.copilot;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Notification> notificationArrayList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Type type = new TypeToken<List<Notification>>() {
        }.getType();

        notificationArrayList = new Gson().fromJson(getIntent().getStringExtra("notification_list"), type);

        Log.d("REcieve in intent","without error");
        Log.d("Array len: ", Integer.toString(notificationArrayList.size()));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

        mMap = googleMap;
        double lat;
        double lon;
        String description;
        LatLng firstPoint = null;

        // get coordinates of the last point
        Notification lastNotification = notificationArrayList.get(0);
        lat = lastNotification.getLat();
        lon = lastNotification.getLon();
        firstPoint = new LatLng(lat, lon);

        if (notificationArrayList != null) {
            // Add a marker in Sydney and move the camera
                Log.d("List: ", "List len is: " + Integer.toString(notificationArrayList.size()));
            for (Notification notification : notificationArrayList) {
                lat = notification.getLat();
                lon = notification.getLon();
                description = notification.getName();
                LatLng newPoint = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(newPoint).title(description));
            }
        }
        else{
            Log.d("List: ","empty");
        }

        if (firstPoint != null) {
            float zoomLevel = 11.5f;
            //mMap.setMaxZoomPreference(10);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPoint,zoomLevel));

        }

//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}