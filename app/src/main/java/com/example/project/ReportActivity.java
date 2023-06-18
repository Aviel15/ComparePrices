package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;

/**
 * activity to report on incorrect price on a specific product
 */
public class ReportActivity extends AppCompatActivity {
    //properties
    private EditText editTextUpdate;                                              //edit text to update
    private static String productName;
    private String address_super_name;
    private Notifications notifications = new Notifications();
    private int row_num = notifications.checkLengthNotifications();               //number of notifications that reported

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        //get id
        editTextUpdate = findViewById(R.id.editTextSale);

        Intent intent = getIntent();                            //get intent
        Bundle data = intent.getExtras();
        if (data != null) {
            productName = data.getString("Product name");       //get message of product name
            address_super_name = data.getString("location");
        }

    }

    /**
     * apply the report and update the new price in db, start the service to send the notification bar
     * @param view - the button apply report
     */
    public void onClick(View view) {
        if(!editTextUpdate.getText().toString().equals(""))
        {
            Intent i = new Intent(this, ComparisonActivity.class);
            i.putExtra("updatePrice", true);                                       //to check when reach to onCreate in ComparisonActivity if need to start the service
            i.putExtra("Product name", productName);
            Product product = new Product();
            product.setProductName(productName);
            product.setAddress_super_name(address_super_name);
            double oldPrice;
            oldPrice = product.getPriceByProduct();

            if (oldPrice != Double.parseDouble(editTextUpdate.getText().toString())) {            //check if the new price is not same like old price
                Intent service = new Intent(this, MyService.class);
                service.putExtra("Product name", productName);
                service.putExtra("boolean", false);                         //false to prevent the notification immediately when report on product
                startService(service);                                                  //start the service
                Toast.makeText(this, "Thank you for a report!", Toast.LENGTH_SHORT).show();

                //update the data base notifications
                notifications = new Notifications(productName, oldPrice, row_num);
                row_num++;

                //update the new price in sql
                double newPrice = Double.parseDouble(editTextUpdate.getText().toString());
                product.setPrice(newPrice);
                try {
                    product.updateProduct();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                startActivity(i);
            }
            else
                Toast.makeText(this, "You enter the same price like was before!", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Enter a new price", Toast.LENGTH_SHORT).show();         //if the edit text of update price is empty
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

    /**
     * return the product name
     * @return - product name
     */
    public static String getProductName() {
        return productName;
    }
}