package com.example.copilot;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class LocationMan implements LocationListener {

    private double lat;
    private double lon;
    private double speed;

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude=location.getLatitude();
        double longitude=location.getLongitude();

        Log.d("Location: ", "Location was changed");
        lat = location.getLatitude();
        if (location.hasSpeed()) {
            speed = location.getSpeed();
        }
        else{
            speed = 0;
        }
        String msg="New Latitude: "+latitude + "New Longitude: "+longitude + "speed: " + speed;
        Log.d("Coordinates: ", msg);
        lon = location.getLongitude();
    }

    public double getLastLat(){
        return lat;
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public double getLastLon(){
        return lon;
    }
}
