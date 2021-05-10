package com.example.copilot;



import androidx.room.Entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;


public class Notification implements  Serializable, Comparable<Notification>{

    //declare private data instead of public to ensure the privacy of data field of each class

    private String name;
    private int danger;
    private int speed;
    private long time;
    private int counter;
    private double lat;
    private double lon;

    public Notification(String name, long time, int danger, int speed, int counter, double lat, double lon) {
        this.name = name;
        this.time = time;
        this.danger = danger;
        this.speed = speed;
        this.counter = counter;
        this.lat = lat;
        this.lon = lon;
    }

    //retrieve user's name
    public String getName(){
        return name;
    }

    public String getCounter(){
        return Integer.toString(counter);
    }
    public long getDate(){
        return time;
    }
    public int getDanger(){
        return  danger;
    }
    public int getSpeed(){
        return speed;
    }
    public double getLat(){
        return this.lat;
    }
    public double getLon(){
        return this.lon;
    }

    public static ArrayList<Notification> getNotifications() {
        ArrayList<Notification> notifications = new ArrayList<Notification>();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        notifications.add(new Notification("semaphore", timestamp.getTime(), 0, 123,0, 1,2));
        notifications.add(new Notification("line_left", timestamp.getTime(), 1, 140,1, 1,2));
        notifications.add(new Notification("frontal_collision", timestamp.getTime(), 0, 340,2,1,2));

        return notifications;
    }

    @Override
    public int compareTo(Notification o) {
        if (name.compareTo(o.name) != 0){
            return  name.compareTo(o.name);
        }
        else return Long.compare(time, o.time);
    }
}

