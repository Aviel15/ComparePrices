package com.example.project;

import static android.os.Build.VERSION_CODES.O;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * activity to compare in table all supermarkets around of specific product
 */
public class ComparisonActivity extends AppCompatActivity {
    //properties
    private static String productName;
    private boolean saleReport = false;         //true if the report is about sale
    private static Location myLocation;         //my current location
    private TextView tvSuper1, tvSuper2, tvSuper3, tvSuper4, tvSuper5;          //text view to all 5 super markets
    private static ArrayList<Product> priceAndSuperName = new ArrayList<>();
    private static int indexOfArray = 0;                                        //the first empty index to present the super market, prevent override other super markets
    private LocationManager locationManager;
    private final static int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;            //id to locations permission
    private final static int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 101;
    private final SuperMarket superMarket = new SuperMarket();
    private final User user = new User();

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparasion);
        //get id
        TextView textViewProduct = findViewById(R.id.textViewProductName);
        tvSuper1 = findViewById(R.id.super1);
        tvSuper2 = findViewById(R.id.super2);
        tvSuper3 = findViewById(R.id.super3);
        tvSuper4 = findViewById(R.id.super4);
        tvSuper5 = findViewById(R.id.super5);

        TextView tvPrice1 = findViewById(R.id.price1);
        TextView tvPrice2 = findViewById(R.id.price2);
        TextView tvPrice3 = findViewById(R.id.price3);
        TextView tvPrice4 = findViewById(R.id.price4);
        TextView tvPrice5 = findViewById(R.id.price5);

        Button btReport1 = findViewById(R.id.report1);
        Button btReport2 = findViewById(R.id.report2);
        Button btReport3 = findViewById(R.id.report3);
        Button btReport4 = findViewById(R.id.report4);
        Button btReport5 = findViewById(R.id.report5);

        CheckBox checkBox1 = findViewById(R.id.checkBoxReport1);
        CheckBox checkBox2 = findViewById(R.id.checkBoxReport2);
        CheckBox checkBox3 = findViewById(R.id.checkBoxReport3);
        CheckBox checkBox4 = findViewById(R.id.checkBoxReport4);
        //check box to check if there are incorrect details
        CheckBox checkBox5 = findViewById(R.id.checkBoxReport5);

        //get permission
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        }
        else { //if already have permissions:
            initLocationListener();
        }

        Intent intent = getIntent();                            //get intent
        Bundle data = intent.getExtras();
        if (data != null) {
            productName = data.getString("Product name");       //get message of product name
            saleReport = data.getBoolean("updatePrice");
        }

        if (saleReport)          //if the report is about sale
        {
            Intent i = new Intent(this, MyService.class);
            startService(i);
        }

        textViewProduct.setText("Product name: " + productName);          //get the name of product from the edit text in search product activity

        //reset indexOfArray and priceAndSuperName to calculate right,
        indexOfArray = 0;
        priceAndSuperName = new ArrayList<>();

        //find all super markets that distance 5 km from current location and save their product name and price in array "priceAndSuperName"
        superMarket.findSuperMarkets();

        //index is to check if has enough super markets to show on the screen
        if(indexOfArray > 0) {
            tvSuper1.setText(priceAndSuperName.get(0).getAddressNameLocation());               //set super name
            tvPrice1.setText(priceAndSuperName.get(0).getPrice()+"");                  //set price
            btReport1.setVisibility(View.VISIBLE);                                      //set button report to visible
            checkBox1.setVisibility(View.VISIBLE);                                      //set check box to visible
        }

        if(indexOfArray > 1) {
            tvSuper2.setText(priceAndSuperName.get(1).getAddressNameLocation());
            tvPrice2.setText(priceAndSuperName.get(1).getPrice()+"");
            btReport2.setVisibility(View.VISIBLE);
            checkBox2.setVisibility(View.VISIBLE);
        }

        if(indexOfArray > 2) {
            tvSuper3.setText(priceAndSuperName.get(2).getAddressNameLocation());
            tvPrice3.setText(priceAndSuperName.get(2).getPrice()+"");
            btReport3.setVisibility(View.VISIBLE);
            checkBox3.setVisibility(View.VISIBLE);
        }

        if(indexOfArray > 3) {
            tvSuper4.setText(priceAndSuperName.get(3).getAddressNameLocation());
            tvPrice4.setText(priceAndSuperName.get(3).getPrice()+"");
            btReport4.setVisibility(View.VISIBLE);
            checkBox4.setVisibility(View.VISIBLE);
        }

        if(indexOfArray > 4) {
            tvSuper5.setText(priceAndSuperName.get(4).getAddressNameLocation());
            tvPrice5.setText(priceAndSuperName.get(4).getPrice()+"");
            btReport5.setVisibility(View.VISIBLE);
            checkBox5.setVisibility(View.VISIBLE);
        }

        LogInActivity logInActivity = new LogInActivity();

        //check if check box was checked, if it is send a toast and cancel the check
        CompoundButton.OnCheckedChangeListener checkListener = (buttonView, isChecked) -> {
            if (isChecked) {
                user.setUsername(logInActivity.getUsername());
                if(user.canReport()) {                                //check if has permissions to add product
                    //can report to more one person there for there are many conditions at same time
                    String whoAdd = "";
                    Product product = new Product();
                    product.setProductName(productName);
                    if (checkBox1.isChecked()) {
                        product.setAddress_super_name(tvSuper1.getText().toString());
                        whoAdd = product.getWhoAdd();
                    }
                    if (checkBox2.isChecked()) {
                        product.setAddress_super_name(tvSuper2.getText().toString());
                        whoAdd = product.getWhoAdd();
                    }
                    if (checkBox3.isChecked()) {
                        product.setAddress_super_name(tvSuper3.getText().toString());
                        whoAdd = product.getWhoAdd();
                    }
                    if (checkBox4.isChecked()) {
                        product.setAddress_super_name(tvSuper4.getText().toString());
                        whoAdd = product.getWhoAdd();
                    }
                    if (checkBox5.isChecked()) {
                        product.setAddress_super_name(tvSuper5.getText().toString());
                        whoAdd = product.getWhoAdd();
                    }
                    // if not reported will save the report other will not saved and send relevant message that you reported on this user in past
                    Report report = new Report(whoAdd, logInActivity.getUsername());
                    user.setUsername(whoAdd);
                    if (report.countWhoAdd() == 5)             //check if the one who report on him is have 5 reports and need to block him
                        user.blockFromPermission();
                }
                else {
                    Toast.makeText(ComparisonActivity.this, "You don't have the permissions", Toast.LENGTH_SHORT).show();          //don't have a permission to report
                    buttonView.setChecked(false);
                }
            }
        };

        //set on click listener
        checkBox1.setOnCheckedChangeListener(checkListener);
        checkBox2.setOnCheckedChangeListener(checkListener);
        checkBox3.setOnCheckedChangeListener(checkListener);
        checkBox4.setOnCheckedChangeListener(checkListener);
        checkBox5.setOnCheckedChangeListener(checkListener);

    }

    /**
     * update the current location to know which show super markets are the closest to me
     */
    public void initLocationListener(){
        //create new location listener object, with min time of 3 seconds and min distance of 20m:
        try {
            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
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
     * move to report activity to report and update on specific product in supermarket
     * @param view - the button that clicked to report
     */
    public void onClickReport(View view) {
        LogInActivity logInActivity = new LogInActivity();
        user.setUsername(logInActivity.getUsername());
        if (user.canReport()) {                                       //check if you have the permissions to report
            //get the location name via tag in text view supers
            String locationName = null;
            if (view.getTag().equals("1"))
                locationName = tvSuper1.getText().toString();
            else if (view.getTag().equals("2"))
                locationName = tvSuper2.getText().toString();
            else if (view.getTag().equals("3"))
                locationName = tvSuper3.getText().toString();
            else if (view.getTag().equals("4"))
                locationName = tvSuper4.getText().toString();
            else if (view.getTag().equals("5"))
                locationName = tvSuper5.getText().toString();

            Intent i = new Intent(this, ReportActivity.class);
            i.putExtra("location", locationName);
            i.putExtra("Product name", productName);
            startActivity(i);
        }
        else
            Toast.makeText(this, "You don't have the permissions", Toast.LENGTH_SHORT).show();      //you don't have the permissions to report
    }

    /**
     * clicked to go back to menu
     * @param view - button to go back to menu
     */
    public void onClickMenu(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /**
     * return the product name
     * @return - the product name
     */
    public static String getProductName() {
        return productName;
    }

    /**
     * return my current location
     * @return - my location
     */
    public static Location getMyLocation() {
        return myLocation;
    }

    /**
     * return the array list with all supermarkets and there price
     * @return - array list pricesAndSuperName
     */
    public static ArrayList<Product> getPriceAndSuperName() {
        return priceAndSuperName;
    }

    /**
     * return the index of array to add a supermarket
     * @return - return the index of array
     */
    public static int getIndexOfArray() {
        return indexOfArray;
    }

    /**
     * set new index
     * @param indexOfArray - set new index array
     */
    public static void setIndexOfArray(int indexOfArray) {
        ComparisonActivity.indexOfArray = indexOfArray;
    }
}