package com.example.copilot;
import android.content.Context;
import android.provider.ContactsContract;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {DatabaseEnity.class}, exportSchema = false, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract NotificationDao notificationDao();
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "notifications_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
