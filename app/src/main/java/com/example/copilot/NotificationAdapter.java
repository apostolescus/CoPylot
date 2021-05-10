package com.example.copilot;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    public NotificationAdapter(@NonNull Context context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.linear_notification, parent, false);
        }

        //convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_700));
        //convertView.setBackgroundColor(getResources().getColor(R.color.teal_700));
        Notification notification = getItem(position);

        //convertView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.alert_background));
        TextView tvTime = (TextView) convertView.findViewById(R.id.textViewAppearanceTime);
        TextView tvMessage = (TextView) convertView.findViewById(R.id.textViewSignCategory);
        TextView tvalertCounter = (TextView) convertView.findViewById(R.id.textViewAlertCounter);
        TextView tvLat = (TextView)convertView.findViewById(R.id.textViewLat);
        TextView tvLon = (TextView)convertView.findViewById(R.id.textViewLon);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewSignIcon);

        String lat = String.valueOf(notification.getLat());
        String lon = String.valueOf(notification.getLon());
        String message = notification.getName();
        Long current_date = notification.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        String formattedDate = sdf.format(current_date);

        tvTime.setText(formattedDate);
        tvLat.setText(lat);
        tvLon.setText(lon);

        switch (message) {
            case "semaphore":
                tvMessage.setText("Semaphore");
                tvMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
                imageView.setImageResource(R.drawable.ic_traffic_semaphore);
                break;
            case "line_left":
                tvMessage.setText("Line Left");
                tvMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
                imageView.setImageResource(R.drawable.ic_traffic_left);
                break;
            case "line_right":
                tvMessage.setText("Line Right");
                tvMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
                imageView.setImageResource(R.drawable.ic_traffic_right);
                break;
            case "pedestrian":
                tvMessage.setText("Pedestrian");
                tvMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.monza));
                imageView.setImageResource(R.drawable.ic_traffic_pedestrian);
                break;
            case "frontal_collision":
                tvMessage.setText("Frontal Collision");
                tvMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.monza));
                imageView.setImageResource(R.drawable.ic_traffic_collision);
                break;
//            case "traffic-sign":
//                tvMessage.setText("Traffic Sign");
//                tvMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.monza));
//                imageView.setImageResource(R.drawable.ic_stop);
//                break;
            case "stop":
                tvMessage.setText("Stop Sign");
                tvMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.monza));
                imageView.setImageResource(R.drawable.ic_traffic_stop);
                break;
            case "give_way":
                tvMessage.setText("Give Way");
                tvMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.monza));
                imageView.setImageResource(R.drawable.ic_traffic_giveway);
                break;

        }

        tvalertCounter.setText(notification.getCounter());
        return  convertView;
    }
}
