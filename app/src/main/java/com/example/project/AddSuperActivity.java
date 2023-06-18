package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * activity add super to repository
 */
public class AddSuperActivity extends AppCompatActivity {
    //properties
    private EditText editTextAddress;            //edit text super address
    private int icon = -1;                       //the icon on the map of supermarket
    private final int[] arraySuperMarketsIcon = {R.drawable.rami_levy, R.drawable.yenot_beitan, R.drawable.osher_ad, R.drawable.shufersal, R.drawable.yohananof};     //array of all supermarkets

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_super);
        //get id
        editTextAddress = findViewById(R.id.editTextAddress);
    }


    /**
     * check if selected icon and entered super address, if the super address exist will add him to map and data base
     * @param view - the button was clicked
     */
    public void onClick(View view) {
        SuperMarket superMarket = new SuperMarket();
        if(icon != -1 && !editTextAddress.getText().toString().equals("")){                 //check if icon selected and edit text address is not null
            Navigator navigator = new Navigator(this, "" + editTextAddress.getText());
            superMarket.setLatitude(navigator.getLatitude());
            superMarket.setLongitude(navigator.getLongitude());
            if(navigator.getLongitude() == 0 && navigator.getLatitude() == 0)             //check if find the address of supermarket!
                Toast.makeText(AddSuperActivity.this,  "Not found the address of supermarket!", Toast.LENGTH_SHORT).show();
            else if(superMarket.superExists())
                Toast.makeText(AddSuperActivity.this,  "This supermarket already exist", Toast.LENGTH_SHORT).show();
            else
            {
                //super market object
                superMarket.insertSuper(editTextAddress.getText().toString(), navigator.getLatitude(), navigator.getLongitude(), icon);         //insert super to db
                Toast.makeText(AddSuperActivity.this,  "Thanks you for added!", Toast.LENGTH_SHORT).show();
            }

            Intent i = new Intent(this, MapShowActivity.class);
            startActivity(i);
        }
        else
            Toast.makeText(AddSuperActivity.this,  "You must to choose a supermarket and address!", Toast.LENGTH_SHORT).show();          //if detect long press on product will add to basket list
    }

    /**
     * set the icon via the radio button that selected
     * @param view - get the radio button is clicked
     */
    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rami_levi_radio:                      //chose 0
                icon = arraySuperMarketsIcon[0];
                break;
            case R.id.yenot_beitan_radio:                   //chose 1
                icon = arraySuperMarketsIcon[1];
                break;
            case R.id.osher_ad_radio:                       //chose 2
                icon = arraySuperMarketsIcon[2];
                break;
            case R.id.shufersal_radio:                      //chose 3
                icon = arraySuperMarketsIcon[3];
                break;
            case R.id.yohananof_radio:                      //chose 4
                icon = arraySuperMarketsIcon[4];
                break;
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