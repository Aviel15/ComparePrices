package com.example.project;

import static android.os.Build.VERSION_CODES.O;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * activity to main page of the app, this page contain transitions to map, basket list and comparasion of prices
 */
public class MainActivity extends AppCompatActivity {
    //properties
    private Button btnSearchProduct, btnMoveToBasket;               //buttons to SearchProductActivity, and BasketListActivity
    private final Notifications notifications = new Notifications();
    private int lengthOfNotifications = notifications.checkLengthNotifications();            //get the current length of notifications, used to check if need to update about new notifications and how much

    @RequiresApi(api = O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get id
        btnSearchProduct = findViewById(R.id.btnSearchProduct);
        btnMoveToBasket = findViewById(R.id.btnMoveBasket);
        TextView textViewWelcome = findViewById(R.id.textViewHello);

        Intent service = new Intent(this, MyService.class);
        service.putExtra("boolean", false);                         //false to prevent the notification immediately when report on product
        startService(service);

        //set text with user name
        LogInActivity logInActivity = new LogInActivity();
        textViewWelcome.setText("Welcome " + logInActivity.getUsername() + "!");

        //array of all permissions needed
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        //request permissions to camera and current location
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 102);
        }

        //call to TIME_TICK broadcast
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        TimeTickReceiver receiver = new TimeTickReceiver();
        registerReceiver(receiver, filter);

    }


    /**
     * if clicked on move to basket will open the basket list activity, if clicked on search product will show all products in repository
     * if clicked on open map will open the map with current location and the location of all supermarkets
     * @param view - the button was clicked
     */
    public void onClick(View view) {
        Intent i;
        if(view == btnMoveToBasket)                                                  //move to basket list activity
            i = new Intent(this, BasketListActivity.class);
        else if(view == btnSearchProduct)                                            //move to search specific product
            i = new Intent(this, SearchProductActivity.class);
        else
            i = new Intent(this, MapShowActivity.class);                //move to map show activity
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * class that extends from broadcast receiver, using in TIME_TICK
     */
    public class TimeTickReceiver extends BroadcastReceiver {
        /**
         * check every minute if have a changes in notification table in SQL
         * if have changes, will send the number of notification as the number of column that added in this minute to phone's users
         * @param context - context
         * @param intent - intent
         */
        @RequiresApi(api = O)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {               //TIME_TICK, occurs every minute
                String product_name;                                  //product name to show in notification
                //check if have a change in db, if yes will update with notification and using service
                while(lengthOfNotifications < notifications.checkLengthNotifications())
                {
                    notifications.setRow_num(lengthOfNotifications);
                    product_name = notifications.getProductNameFromSpecificRowNotifications();     //get product name vid row number in db
                    lengthOfNotifications++;
                    Intent serviceIntent = new Intent(context, MyService.class);                    //intent to MyService

                    serviceIntent.putExtra("toReport", true);                            //to show the notification only when the TIME_TICK is occurs and not immediately when report on product
                    serviceIntent.putExtra("Product name", product_name.trim());

                    ContextCompat.startForegroundService(MainActivity.this, serviceIntent);                                            //start service
                }
            }
        }
    }
}