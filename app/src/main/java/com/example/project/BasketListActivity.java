package com.example.project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Activity to basket list, show the current basket list view with the amount and product's name
 */
public class BasketListActivity extends AppCompatActivity {
    //properties
    /**
     * array list that contain the all names product
     */
    public static ArrayList<String> productNameArrayList = new ArrayList<>();                 //array list to products name
    private static ArrayList<Integer> dataArrayListAmount = new ArrayList<>();                 //array list to amounts
    private static ListView listView;
    private ListViewAdapter adapter;
    private String productName;
    private Button btnResetList, btnAffordableBuy;                                              //buttons to reset basket list and move to ResultActivity
    private LocationManager locationManager;
    private final static int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;            //id to locations permission
    private final static int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 101;
    private static Location myLocation;                                                         //my current location
    private BasketList basketList;
    private final ArrayList<String> products_exists = new ArrayList<>();

    /**
     * array list that contain Objects of SuperMarkets, supermarket is each product in list view
     */
    public static ArrayList<SuperMarket> basketListSuperMarkets = new ArrayList<>();                  //array list of all supermarkets
    /**
     * calculate the total price for the most affordable buy
     */
    public static double total_price = Double.MAX_VALUE;                                              //the total price of buy

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.basket_list);
        //get id
        listView = findViewById(R.id.listview);
        btnResetList = findViewById(R.id.btnMoveResults2);
        btnAffordableBuy = findViewById(R.id.btnMoveResults);

        basketList = new BasketList();
        //get names and amounts
        productNameArrayList = basketList.getAllBasketListName();
        dataArrayListAmount = basketList.getAllBasketListAmount();

        //set adapter
        adapter = new ListViewAdapter(this, productNameArrayList, dataArrayListAmount);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        Intent intent = getIntent();                            //get intent
        Bundle data = intent.getExtras();
        if (data != null)
            productName = data.getString("product_name");       //get message of product name
        basketList.setProduct_name(productName);
        //if the product is exist the product
        if(productName != null && basketList.basketListProductExist())
            addProduct();

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

    }


    /**
     * add product to basket list
     */
    public void addProduct()
    {
        listView.setItemsCanFocus(true);
        adapter = new ListViewAdapter(this, productNameArrayList, dataArrayListAmount);
        listView.setAdapter(adapter);
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
     * if clicked on best buy button will show the mose affordable buy, if clicked on reset basket button will reset all basket list
     * and if clicked on apply changes button will save all changes in data base
     * @param view - the button was clicked
     */
    public void onClick(View view) throws SQLException {
        if(view == btnAffordableBuy)                        //move to ResultActivity
        {
            if(listView.getChildCount() > 0) {                          //if list view is not empty
                //calculate which super is in radius of less 10 km
                SuperMarket superMarket = new SuperMarket();
                superMarket.setLocation(myLocation);
                basketListSuperMarkets = superMarket.getSuperMarketsInRadius();         //return all super markets in radius of 5 km
                //check which super has most products available and calculate the lowest buy
                superMarket.findSuperMarketsWithMostProducts();
                if(products_exists.size() == 0)
                    check_products_exists(basketListSuperMarkets.get(0).getLatitude(), basketListSuperMarkets.get(0).getLongitude());
                Intent i = new Intent(this, ResultActivity.class);
                //put extra the three super markets name and their final price of all basket list
                i.putExtra("super_name", basketListSuperMarkets.get(0).getName());
                i.putExtra("total_price", total_price);
                i.putExtra("products_exists", products_exists);
                startActivity(i);
            }
            else
                Toast.makeText(this, "Your basket list is empty", Toast.LENGTH_SHORT).show();
        }
        else if(view == btnResetList)
        {
            basketList.removeAllBasketList();                               //remove all products from basket list and update that in db
            productNameArrayList = basketList.getAllBasketListName();
            adapter = new ListViewAdapter(this, productNameArrayList, dataArrayListAmount);
            listView.setAdapter(adapter);
        }
        else                    //apply changes button - save the amounts in data base
        {
            if(listView.getChildCount() > 0) {
                boolean amountsFilled = true;
                HashMap<String, Integer> basketListHash = new HashMap<>();
                for (int i = 0; i < listView.getChildCount(); i++)
                    if (Objects.requireNonNull(getTextEditText(i)).equals(""))                               //if all edit text in basket list is not empty
                        amountsFilled = false;

                for (int i = 0; i < listView.getChildCount(); i++) {
                    if (amountsFilled)                             //if the amount edit text is not empty
                        basketListHash.put(getTextTextView(i), Integer.parseInt(Objects.requireNonNull(getTextEditText(i))));
                    else
                        break;
                }
                if (amountsFilled) {
                    Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show();
                    /*
                    update the sql with hashmap
                     Insert values - amounts
                    */
                    ArrayList<String> productNameKey = new ArrayList<>(basketListHash.keySet());
                    int position = 0;               //the index
                    for (Integer i : basketListHash.values()) {
                        basketList.setProduct_name(productNameKey.get(position));
                        basketList.setAmount(i);
                        basketList.insertBasketList();               //insert to db the amount and his product name
                        position++;
                    }
                } else                                  //if not all edit text filled
                    Toast.makeText(this, "You must fill all the amounts in basket list", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "Your basket list is empty", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * check which products in basket list exists in specific location
     * @param latitude - latitude
     * @param longitude - longitude
     * @throws SQLException - sql exception
     */
    public void check_products_exists(double latitude, double longitude) throws SQLException {
        int size = productNameArrayList.size();
        int counter = 0;
        Product product = new Product();
        product.setLatitude(latitude);
        product.setLongitude(longitude);

        while(counter < size)
        {
            product.setProductName(productNameArrayList.get(counter));
            if(product.productExistByLocation())
                products_exists.add(product.getProductName());
            counter++;
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
     * method to get text in specific position from edit text in list view
     * @param position - the position in list view
     * @return return the text in edit text
     */
    public static String getTextEditText(int position)
    {
        EditText editText = listView.getChildAt(position).findViewById(R.id.editText);
        if(editText == null)
            return null;
        return editText.getText().toString();
    }


    /**
     * method to get text in specific position from text view in list view
     * @param position - the position in list view
     * @return return the text in text view
     */
    public static String getTextTextView(int position)
    {
        TextView textView = listView.getChildAt(position).findViewById(R.id.name);
        if(textView == null)
            return null;
        return textView.getText().toString();
    }

    /**
     * method to get the list
     * @return return productNameArrayList
     */
    //get list product name
    public ArrayList<String> getList() {
        return productNameArrayList;
    }

}