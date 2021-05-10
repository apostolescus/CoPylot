package com.example.copilot;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity(tableName = "notifications")
public class DatabaseEnity {

    @PrimaryKey(autoGenerate = true)
    public int uuid;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    @ColumnInfo(name="danger")
    private int danger;

    @ColumnInfo(name="speed")
    private int speed;

    @ColumnInfo(name="name")
    private String name;

    public DatabaseEnity(){

    }

    public DatabaseEnity(Notification notification){
        this.timestamp = notification.getDate();
        this.danger = notification.getDanger();
        this.speed = notification.getSpeed();
        this.name = notification.getName();
    }

    long getTimestamp(){
        return timestamp;
    }
    int getDanger(){
        return danger;
    }
    int getSpeed(){
        return speed;
    }
    String getName() {
        return name;
    }

    void setTimestamp(long timestamp1){
        timestamp = timestamp1;
    }
    void setDanger(int danger1){
        danger = danger1;
    }
    void setSpeed(int speed1){
        speed = speed1;
    }
    void setName(String name1){
        name = name1;
    }

}
