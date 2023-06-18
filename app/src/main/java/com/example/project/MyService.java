package com.example.project;

import static android.app.PendingIntent.getActivity;
import static android.os.Build.VERSION_CODES.O;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

/**
 * running background to notify about new notifications also when the app is closed
 */
public class MyService extends Service {
    //properties
    private final String CHANNEL_ID = "notify";
    private String productName;
    private static final ArrayList<String> toReport = new ArrayList<>();
    private boolean flag;
    private static int notificationId = 0;
    private NotificationManager notificationManager;

    /**
     * called when use startService()
     */
    @RequiresApi(api = O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle data = intent.getExtras();
        if (data != null) {
            productName = data.getString("Product name");       //get message of product name
            flag = data.getBoolean("toReport");
        }

        if(!toReport.contains(productName))
            toReport.add(productName);              //add to array list the product name


        MyRoutineTask routineTask = new MyRoutineTask();
        routineTask.start();

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void createForegroundNotification() {
        //the method creates the foreground Notification, which make the service run in background when the app is closed
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("My Service")
                .setContentText("Running in the foreground")
                .setAutoCancel(true);
        startForeground(1, builder.build());
    }

    @RequiresApi(api = O)
    @Override
    public void onCreate() {
        createNotificationChannel();                //create notification channel
        createForegroundNotification();             //create notification background to allow send notifications also even the app is closed
        notificationId++;
    }


    /**
     * create notification channel
     */
    @RequiresApi(api = O)
    public void createNotificationChannel()
    {
        //create notification manager
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Sale!", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Sale!");
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * create notification bar and notify
     */
    @RequiresApi(api = O)
    public void createNotification(String productName) {
        Intent resultIntent = new Intent(this, SearchProductActivity.class);

        PendingIntent pendingIntent = getActivity(this, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);

        //build notification:
        Notification notification = new Notification.Builder(MyService.this, CHANNEL_ID)
                .setContentTitle("You have a sale on product " + productName)
                .setContentText("Click to enter to read message")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.sale_notification)
                .build();

        notificationManager.notify(notificationId, notification);
        notificationId++;
        flag = false;
    }

    /**
     * onBind
     */
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * onTaskRemoved
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }


    /**
     *
     */
    private class MyRoutineTask extends Thread {
        @RequiresApi(api = O)
        @Override
        public void run() {
            if(flag) {
                for (int i = 0; i < toReport.size(); i++) {
                    if(toReport.get(i) != null)             //do not create notification if the product name is null
                        createNotification(toReport.get(i));
                }
                toReport.clear();
            }
        }
    }
}