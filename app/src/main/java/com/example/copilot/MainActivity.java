package com.example.copilot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> items = new ArrayList<>();

    private ArrayList<Notification> arrayOfNotifications = null;

    private NotificationAdapter adapter;
    private int alertCounter = 0, intTotalTimeHours = 0, intTotalTimeMins = 0;
    private TextView tvTotalTime, tvTotalMinutes, tvIncidentNumbers;

    Thread thread = new Thread() {
        @Override
        public void run() {
            try {
                while(true) {

                    if (intTotalTimeMins == 60) {
                        intTotalTimeHours += 1;
                        intTotalTimeMins = 0;
                        updateTime(Integer.toString(intTotalTimeHours), true);
                        updateTime(Integer.toString(intTotalTimeMins), false);
                    }else {
                        updateTime(Integer.toString(intTotalTimeMins), false);
                    }
                    Log.d("---Thread---", "Updated time: " + Integer.toString(intTotalTimeHours));
                    sleep(60000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    public void updateTime(String value, boolean mode) {
        final String str = value;

        if (!mode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTotalMinutes.setText(str);
                    Log.d("--- update Time --- ", "Text seted!" + str);
                }
            });
        } else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTotalTime.setText(str);
                    Log.d("--- update Time --- ", "Text seted!" + str);
                }
            });
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        tvTotalTime = (TextView) findViewById(R.id.textViewTime);
        tvTotalMinutes = (TextView) findViewById(R.id.textViewTimeMinutes);
        tvIncidentNumbers = (TextView) findViewById(R.id.textViewIncidentNumbers);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("drivingInfos");

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Double timestamp = (Double) ds.child("time").getValue();
                    long itemLong = (long) (timestamp * 1000);
                    Date itemDate = new Date(itemLong);

                    int speed = Integer.parseInt(String.valueOf(ds.child("speed").getValue()));
                    int danger = Integer.parseInt(String.valueOf(ds.child("danger").getValue()));

                    String alert_type =  ds.child("type").getValue(String.class);
                    arrayOfNotifications.add(0,new Notification(alert_type, itemDate, danger, speed, alertCounter));

                    tvIncidentNumbers.setText(Integer.toString(alertCounter));

                    adapter.notifyDataSetChanged();
                    alertCounter++;

                    Toast.makeText(getApplicationContext(),"New notification", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(),"Alert is: " + alert_type, Toast.LENGTH_SHORT).show();
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to read the value", Toast.LENGTH_SHORT).show();
            }
        });

        thread.start();

//        try {
//            FileInputStream file = openFileInput("cities.dat");
//            ObjectInputStream inputStream = new ObjectInputStream(file);
//            notifications = (List<Notification>) inputStream.readObject();
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }


//        SharedPreferences sharedPreferences = getSharedPreferences("citites", MODE_PRIVATE);
//        Set<String> set = sharedPreferences.getStringSet("names", null);
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

//        if (set != null){
//            notifications.clear();
//            for (String s: set){
//                String[] values = s.split(", ");
//                try {
//                    Date date = formatter.parse(values[1]);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                Notification notification = new Notification(values[0], date);
//                notifications.add(notification);
//            }
//        }

        //populateNotificationList();
        readExistingAlerts();

        Button btn = (Button)findViewById(R.id.buttonTest);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayOfNotifications.add(0,new Notification("semaphore", new Date(), 0, 234,alertCounter));
                adapter.notifyDataSetChanged();
                alertCounter++;
                Toast.makeText(getApplicationContext(),"New notification"
                        , Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        //arrayOfNotifications.add(new Notification("semaphore", new Date()));

//        try {
//            FileOutputStream file = openFileOutput("notification.dat", MODE_PRIVATE);
//            ObjectOutputStream outputStream = new ObjectOutputStream(file);
//            outputStream.writeObject(arrayOfNotifications);
//            outputStream.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        SharedPreferences sharedPreferences = getSharedPreferences("cities", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Set <String> set = new TreeSet<>();

//        for (Notification notification: notifications){
//            set.add(notification.toString());
//        }
//        editor.putStringSet("names", set);
//        editor.commit();

    }

    private void readExistingAlerts(){

        //try get notifications from firebase

        arrayOfNotifications = new ArrayList<Notification>();
        adapter = new NotificationAdapter(this, arrayOfNotifications);
        ListView listView = (ListView) findViewById(R.id.theList);
        listView.setAdapter(adapter);
    }
    private void populateNotificationList(){
        try {
            FileInputStream file = openFileInput("notification.dat");
            ObjectInputStream inputStream = new ObjectInputStream(file);
            arrayOfNotifications = (ArrayList<Notification>) inputStream.readObject();
            inputStream.close();
        } catch (IOException e) {
            System.out.println("Fisierul nu exista");
            arrayOfNotifications = Notification.getNotifications();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        adapter = new NotificationAdapter(this, arrayOfNotifications);

        ListView listView = (ListView) findViewById(R.id.theList);
        listView.setAdapter(adapter);
    }
}