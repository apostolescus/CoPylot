package com.example.copilot;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;

import java.util.List;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM notifications")
    List<DatabaseEnity> getAll();

    @Query("SELECT * FROM notifications WHERE danger = 0")
    List<DatabaseEnity> loadAllDangerous();

    @Insert
    void insertAll(DatabaseEnity ... databaseEnities);

    @Query("DELETE FROM notifications")
    void deleteAll();
}
