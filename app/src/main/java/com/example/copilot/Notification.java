package com.example.copilot;

import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// lane cross; collision detection
import java.util.ArrayList;

public class Notification implements  Serializable, Comparable<Notification>{

    //declare private data instead of public to ensure the privacy of data field of each class
    private String name;
    private int danger;
    private int speed;
    private Date time;

    public Notification(String name, Date time, int danger, int speed) {
        this.name = name;
        this.time = time;
        this.danger = danger;
        this.speed = speed;
    }

    //retrieve user's name
    public String getName(){
        return name;
    }


    public Date getDate(){
        return time;
    }

    public static ArrayList<Notification> getNotifications() {
        ArrayList<Notification> notifications = new ArrayList<Notification>();

        Date current_date = new Date();

        notifications.add(new Notification("semaphore", current_date, 0, 123));
        notifications.add(new Notification("line_left", current_date, 1, 140));
        notifications.add(new Notification("frontal_collision", current_date, 0, 340));

        return notifications;
    }

    @Override
    public int compareTo(Notification o) {
        if (name.compareTo(o.name) != 0){
            return  name.compareTo(o.name);
        }
        else{
            return time.compareTo(o.time);
        }
    }
}