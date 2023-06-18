package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;

/**
 * activity to search a specific product form the repository
 */
public class SearchProductActivity extends AppCompatActivity {
    private ListView listView;                                          //list view of all products
    private BasketList basketList;
    /**
     * adapter to list view
     */
    public static ArrayAdapter<String> adapter;                         //adapter
    /**
     * contain all products in list view
     */
    public static ArrayList<String> arrayList = new ArrayList<>();      //array list
    private Button btnAddToRepository;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        //get id
        //properties
        //edit text search product
        EditText etSearch = findViewById(R.id.editTextSearch);
        listView = findViewById(R.id.listViewProducts);
        listView.setTextFilterEnabled(true);
        btnAddToRepository = findViewById(R.id.buttonAddToRepository);

        Product product = new Product();
        //get all products from data base
        arrayList = product.getAllProducts();
        adapter  = new ArrayAdapter<>(SearchProductActivity.this, R.layout.custom_textview, arrayList);
        listView.setAdapter(adapter);

        TextWatcher textWatcher = new TextWatcher() {
        //text watcher, filter results when search product in edit text

            /**
             * used to listen for changes in a text field.
             * @param arg0 - the current text in the text field.
             * @param arg1 - the start position of the text that was changed.
             * @param arg2 - the number of characters that were changed.
             * @param arg3 - the after position of the text that was changed.
             */
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(arg0.toString());
            }

            /**
             * this method is called before the text in the text field is changed by the user.
             * @param arg0 - the current text in the text field.
             * @param arg1 - the start position of the text that will changed.
             * @param arg2 - the number of characters that will changed.
             * @param arg3 - the after position of the text that will changed.
             */
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            /**
             * this method is called after the text in the text field is changes by the user
             * @param arg0 - the current text in the text field.
             */
            @Override
            public void afterTextChanged(Editable arg0) {

            }
        };

        etSearch.addTextChangedListener(textWatcher);

        Intent intent = getIntent();                            //get intent
        Bundle dataBundle = intent.getExtras();
        if (dataBundle != null) {
            //barcode number
            String product_name = dataBundle.getString("product_name");       //get message of product name
            if (product_name != null && !product_name.equals("null"))                                            //check if product name is not null, if found a real product
            {
                etSearch.setText(product_name);
                etSearch.addTextChangedListener(textWatcher);
            }
        }

        //detect long press - add to basket list
        listView.setOnItemLongClickListener((arg0, v, index, arg3) -> {
            String str = listView.getItemAtPosition(index).toString();
            basketList = new BasketList();
            basketList.setProduct_name(str);
            if(!basketList.basketListProductExist())                         //check if the product exist in basket list to know if add or no
            {
                basketList.insertProductNameBasketList();                       //insert to basket list
                Toast.makeText(SearchProductActivity.this, str.trim() + " added to basket list", Toast.LENGTH_SHORT).show();          //if detect long press on product will add to basket list

                Intent i = new Intent(SearchProductActivity.this, BasketListActivity.class);             //move to basket list activity
                i.putExtra("product_name", str);
                startActivity(i);
            }
            else
                Toast.makeText(SearchProductActivity.this, str.trim() + " already exist in basket list", Toast.LENGTH_SHORT).show();          //if detect long press on product will add to basket list
            return true;
        });

        //detect short press - move to ComparisonActivity about the specific product
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String productName = listView.getItemAtPosition(position).toString();
            Intent i = new Intent(SearchProductActivity.this, ComparisonActivity.class);             //move to add the comparison activity
            i.putExtra("Product name", productName);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

    }

    /**
     * if clicked on add product will move to AddProductActivity, if clicked on the scanner image button will open the camera and scan the barcode
     * @param view - the button that clicked
     */
    public void onClick(View view) {
        Intent i = new Intent(this, AddProductActivity.class);
        boolean canReport = true;
        LogInActivity logInActivity = new LogInActivity();

        //move to AddProductActivity
        if (view == btnAddToRepository) {
            User user = new User();
            user.setUsername(logInActivity.getUsername());
            if(!user.canReport()) {                                //check if has permissions to add product
                canReport = false;                                                               //don't have a permission to report
            }
        }
        else {
            i = new Intent(this, ScannerProduct.class);
            i.putExtra("screen", 2);                                            //to identify if it call from search product activity of add product activity
            startActivity(i);
        }
        if(canReport)
            startActivity(i);
        else
            Toast.makeText(this, "You don't have the permissions", Toast.LENGTH_SHORT).show();
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