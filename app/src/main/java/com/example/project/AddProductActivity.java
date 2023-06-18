package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;

/**
 * activity add product to repository
 */
public class AddProductActivity extends AppCompatActivity {
    //properties
    private EditText etProductName, etPrice, etSuperAddress;            //edit text to product name, price and super address
    private Button btnAddProduct;
    private boolean integrityCheck;                                     //check if all details is valid
    private Product product = new Product();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_to_service);
        //get id
        etProductName = findViewById(R.id.editTextProductName);
        etPrice = findViewById(R.id.editTextPrice);
        etSuperAddress = findViewById(R.id.editTextSuperAddress);
        btnAddProduct = findViewById(R.id.buttonAddProduct);

        Intent intent = getIntent();                            //get intent
        Bundle dataBundle = intent.getExtras();
        if (dataBundle != null) {
            //barcode number
            String product_name = dataBundle.getString("product_name");       //get message of product name
            if(product_name != null && !product_name.equals("null"))                                            //check if product name is not null, if found a real product
                etProductName.setText(product_name);
        }
    }

    /**
     * if clicked on add product button will move to add product activity
     * if clicked on image button scanner will open the camera and scan the barcode
     * @param view - the button was clicked
     * @throws SQLException - throw exception of SQL
     */
    public void onClick(View view) throws SQLException {
        LogInActivity logInActivity = new LogInActivity();
        SuperMarket superMarket = new SuperMarket();

        if(view == btnAddProduct) {                                             //add the product to activity
            integrityCheck();
            if(integrityCheck) {
                Navigator navigator = new Navigator(this, etSuperAddress.getText().toString());             //the location of super market
                superMarket.setLatitude(navigator.getLatitude());
                superMarket.setLongitude(navigator.getLongitude());
                String productName = etProductName.getText().toString().trim();
                product.setProductName(productName);
                product.setLatitude(navigator.getLatitude());
                product.setLongitude(navigator.getLongitude());
                if (!product.productExistByLocation()) {                           //check if the product exist, if not the product will add to application
                    double price = Double.parseDouble(etPrice.getText().toString());
                    if (navigator.getLongitude() == 0 && navigator.getLatitude() == 0)
                        Toast.makeText(AddProductActivity.this, "This address is not exist!", Toast.LENGTH_SHORT).show();          //if detect long press on product will add to basket list
                    else if (superMarket.getSuperMarketByLocation() != null) {                 //get SuperMarket object by location
                        if (!SearchProductActivity.arrayList.contains(productName)) {
                            SearchProductActivity.arrayList.add(etProductName.getText().toString());
                            SearchProductActivity.adapter.notifyDataSetChanged();
                        }
                        Toast.makeText(AddProductActivity.this, "Thanks you for added!", Toast.LENGTH_SHORT).show();          //if detect long press on product will add to basket list
                        product = new Product(productName, logInActivity.getUsername(), price, navigator.getLatitude(), navigator.getLongitude(), etSuperAddress.getText().toString());

                    } else
                        Toast.makeText(AddProductActivity.this, "Not found the address, try to add in map screen!", Toast.LENGTH_SHORT).show();          //if detect long press on product will add to basket list
                    Intent i = new Intent(this, SearchProductActivity.class);       //go back to search product activity
                    startActivity(i);
                }
                else
                    Toast.makeText(AddProductActivity.this, "This product already exist in repository", Toast.LENGTH_LONG).show();          //if the product already exist in repository
            }
        }
        else
        {
            Intent i = new Intent(this, ScannerProduct.class);       //go back to search product activity
            i.putExtra("screen", 1);
            startActivity(i);
        }
    }

    //check if all field in edit text are ok
    private void integrityCheck() {
        integrityCheck = true;
        String priceText = etPrice.getText().toString().trim();
        String productNameText = etProductName.getText().toString().trim();
        String superAddressText = etSuperAddress.getText().toString().trim();

        if (priceText.isEmpty()) {                                      //check if price text is empty
            etPrice.setError("price is required!");
            etPrice.requestFocus();
            integrityCheck = false;
        }
        if (productNameText.isEmpty()) {                                //check if product name text is empty
            etProductName.setError("product name is required!");
            etProductName.requestFocus();
            integrityCheck = false;
        }
        if (superAddressText.isEmpty()) {                               //check if super address text
            etSuperAddress.setError("super address is required!");
            etSuperAddress.requestFocus();
            integrityCheck = false;
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
}