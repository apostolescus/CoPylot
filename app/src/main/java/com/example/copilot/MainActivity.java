package com.example.copilot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
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
    protected LocationManager locationManager;
    private LocationMan locationListener;
    Context mContext;
    Criteria cri = new Criteria();

    private int alertCounter = 0, intTotalTimeHours = 0, intTotalTimeMins = 0;
    private TextView tvTotalTime, tvTotalMinutes, tvIncidentNumbers;
    private String databaseMode = "write";
    private boolean rec = false;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;


    private void play_alert_sound() {
        Uri alarmSound =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), alarmSound);
        mp.start();
    }

    Thread updateDb = new Thread() {
        @Override
        public void run() {
            // save all notifications
            AppDatabase.getDatabase(getApplicationContext()).notificationDao().deleteAll();

            for (Notification notification : arrayOfNotifications) {
                DatabaseEnity databaseEnity = new DatabaseEnity(notification);
                AppDatabase.getDatabase(getApplicationContext()).notificationDao().insertAll(databaseEnity);
            }

            Log.d("--- database ---", "trip saved successfully");
            // updateDatabase()

        }
    };

    Thread thread = new Thread() {
        @Override
        public void run() {
            try {
                while (true) {
                    if (intTotalTimeMins == 60) {
                        intTotalTimeHours += 1;
                        intTotalTimeMins = 0;
                        updateTime(Integer.toString(intTotalTimeHours), true);
                        updateTime(Integer.toString(intTotalTimeMins), false);
                    } else {
                        intTotalTimeMins++;
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
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTotalTime.setText(str);
                    Log.d("--- update Time --- ", "Text seted!" + str);
                }
            });
        }

    }

    @SuppressLint("MissingPermission")
    private void getLocation(){
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tvTotalTime = (TextView) findViewById(R.id.textViewTime);
        tvTotalMinutes = (TextView) findViewById(R.id.textViewTimeMinutes);
        tvIncidentNumbers = (TextView) findViewById(R.id.textViewIncidentNumbers);

        //initate location manager
        mContext = this;

        //check for permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        locationListener = new LocationMan();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("drivingInfos");

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String ts = ds.child("time").getValue().toString();
                    Timestamp tmstp = new Timestamp(System.currentTimeMillis());

                    double lat, lon;
                    int speed = Integer.parseInt(String.valueOf(ds.child("speed").getValue()));
                    int danger = Integer.parseInt(String.valueOf(ds.child("danger").getValue()));
                    getLocation();
//                    if (danger == 1){
//                        play_alert_sound();
//                    }

                    lat = locationListener.getLastLat();
                    lon = locationListener.getLastLon();

                    String alert_type =  ds.child("type").getValue(String.class);
                    arrayOfNotifications.add(0,new Notification(alert_type, tmstp.getTime(), danger, speed, alertCounter, lat, lon));

                    tvIncidentNumbers.setText(Integer.toString(alertCounter));

                    adapter.notifyDataSetChanged();
                    alertCounter++;

                    //Toast.makeText(getApplicationContext(),"New notification", Toast.LENGTH_SHORT).show();
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
        readExistingAlerts();

        Button btn = (Button)findViewById(R.id.buttonTest);
        Button updateButton = (Button) findViewById(R.id.buttonUpdateDb);
        Button btnStartTrip = (Button) findViewById(R.id.buttonStartTrip);
        Button btnLoadDB = (Button) findViewById(R.id.buttonLoadDB);
        ImageView mapImage = (ImageView) findViewById(R.id.imageViewMap);

        mapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Before Intent", "List len is: " + Integer.toString(arrayOfNotifications.size()));


                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("notification_list", new Gson().toJson(arrayOfNotifications));
                startActivity(intent);
            }
        });

        btnLoadDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayOfNotifications.clear();
                adapter.notifyDataSetChanged();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("--- read database ---", "Reading from database");
                        List<DatabaseEnity> databaseEnityList = AppDatabase.getDatabase(getApplicationContext()).notificationDao().getAll();
                        for (DatabaseEnity databaseEnity: databaseEnityList){
                            Notification notification = new Notification(databaseEnity.getName(), databaseEnity.getTimestamp(),
                                    databaseEnity.getDanger(), databaseEnity.getSpeed(), 1, 44.567, 54.675);
                            arrayOfNotifications.add(notification);
                        }
                        Log.d("read database", "List len is: " + Integer.toString(arrayOfNotifications.size()));
                        Log.d("---read database---", "Elements successfully read");
                        v.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();

//                readDb.start();
//                adapter.notifyDataSetChanged();
            }
        });
        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTripRecording();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDb.start();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timestamp tmstp = new Timestamp(System.currentTimeMillis());
                getLocation();
                arrayOfNotifications.add(0,new Notification("semaphore", tmstp.getTime(), 0, 234,alertCounter, locationListener.getLastLat(), locationListener.getLastLon()));
                adapter.notifyDataSetChanged();
                alertCounter++;
                Toast.makeText(getApplicationContext(),"New notification"
                        , Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void startTripRecording(){

        arrayOfNotifications.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Started Trip Rec", Toast.LENGTH_SHORT).show();
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

}