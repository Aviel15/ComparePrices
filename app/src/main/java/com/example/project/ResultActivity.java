package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.sql.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * activity to show the result about the most affordable place to buy to list
 */
public class ResultActivity extends AppCompatActivity {
    private String super_name;
    private double total_price = 0;
    private ArrayList<String> products_exists = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //get id
        //properties
        TextView super_name_tv = findViewById(R.id.super_name);
        TextView price_total_tv = findViewById(R.id.priceTotal);
        TextView products_exist_tv = findViewById(R.id.products_exist);

        Intent intent = getIntent();                            //get intent
        if (intent != null)
        {
            Bundle data = intent.getExtras();
            if (data != null) {
                super_name = data.getString("super_name");       //get message of product name
                total_price = data.getDouble("total_price");
                products_exists = data.getStringArrayList("products_exists");
            }
        }

        DecimalFormat df = new DecimalFormat("#.##");
        String formattedNumber = df.format(total_price);                //get the two number after the point


        StringBuilder stringBuilder = new StringBuilder();
        int lastIndex = products_exists.size() - 1; // Get the index of the last element

        for (int i = 0; i < products_exists.size(); i++) {
            String value = products_exists.get(i);
            stringBuilder.append(value);

            if (i != lastIndex) {
                stringBuilder.append(", ");
            }
        }


        String valuesString = stringBuilder.toString().trim();

        //set text
        products_exist_tv.setText(valuesString);
        super_name_tv.setText(super_name);
        price_total_tv.setText(formattedNumber+"");
        products_exists = new ArrayList<>();
    }


    /**
     * go back to BasketListActivity
     * @param view - clicked on button to move to BasketListActivity
     */
    public void onClickResult(View view) {
        Intent i = new Intent(this, BasketListActivity.class);
        startActivity(i);
    }

    /**
     * create option menu
     * @param menu - Menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * check if item selected, if yes will go back to menu
     * @param item - MenuItem
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item) {
            Intent i = new Intent(this, MainActivity.class);             //move to comparison of specific product
            startActivity(i);
        }
        return true;
    }
}