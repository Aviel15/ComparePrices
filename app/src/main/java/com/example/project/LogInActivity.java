package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

/**
 * activity to log in page, contain user and password edit texts to check valid user
 */
public class LogInActivity extends AppCompatActivity {
    //properties
    private Button btnSignUp, btnLogIn;              //buttons to log in and sign up
    private ImageView image;                         //image GIF
    private TextInputLayout username, password;
    private static String usernameText;
    private boolean validDetails = true;
    private CheckBox checkBox;                       //to remember user details
    private SharedPreferences.Editor editor;
    private final User user = new User();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_log_in);
        //get id
        btnSignUp = findViewById(R.id.buttonSignUp);
        btnLogIn = findViewById(R.id.buttonLogIn);
        image = findViewById(R.id.gifImageView);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        checkBox = findViewById(R.id.checkBox);

        MySql.connect();                //connect to data base

        SharedPreferences preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);         //create shared preferences
        editor = preferences.edit();

        Objects.requireNonNull(username.getEditText()).setText(preferences.getString("username", ""));                  //get from shared preferences the details that saved
        Objects.requireNonNull(password.getEditText()).setText(preferences.getString("password", ""));

    }

    /**
     * move to sign up page with animation
     * @param view - the button to move sign up page
     */
    //on click move to sign up page with animation
    public void signup(View view) {
        Intent i = new Intent(this,SignUpActivity.class);

        Pair[] pairs = new Pair[5];                                 //all views

        pairs[0] = new Pair<View, String>(image, "logo_image");
        pairs[1] = new Pair<View, String>(btnSignUp, "sign_up_tran");
        pairs[2] = new Pair<View, String>(btnLogIn, "button_tran");
        pairs[3] = new Pair<View, String>(username, "username_tran");
        pairs[4] = new Pair<View, String>(password, "password_tran");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LogInActivity.this, pairs);
        startActivity(i, options.toBundle());
    }


    /**
     * move to main page, log in to app, check if all details of user is valid
     * @param view - the button to move MainActivity
     */
    public void btnLogIn(View view) {
        userLogin();           //check valid details
        if(validDetails) {
            if(checkBox.isChecked())
            {
                String textUsername = Objects.requireNonNull(username.getEditText()).getText().toString();                  //apply new details to save in shared preferences
                String textPass = Objects.requireNonNull(password.getEditText()).getText().toString();

                editor.putString("username", textUsername);
                editor.putString("password", textPass);
                editor.apply();
            }
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private void userLogin() {
        //check if all field in edit text are ok
        validDetails = true;
        usernameText = Objects.requireNonNull(username.getEditText()).getText().toString().trim();
        String passwordText = Objects.requireNonNull(password.getEditText()).getText().toString().trim();

        if(usernameText.isEmpty())                              //check if user name text is empty
        {
            username.setError("Username is required!");
            username.requestFocus();
            validDetails = false;
        }
        else
            username.setError(null);

        if(passwordText.isEmpty())                              //check if password text is empty
        {
            password.setError("Password is required!");
            password.requestFocus();
            validDetails = false;
        }
        else
            password.setError(null);

        if(passwordText.length() > 15)                      //check if password text length is more than 15
        {
            password.setError("Max length is 15!");
            password.requestFocus();
            validDetails = false;
        }
        else {
            password.setError(null);
        }

        boolean userExist;
        passwordText = HashingPassword.hashPassword(passwordText);                  //hash the password to deny from cyber attacks
        user.setUsername(usernameText);
        user.setPassword(passwordText);
        userExist = user.usernameAndPasswordExists();
        if(!userExist)                                                              //check in data base if exist this user
        {
            Toast.makeText(this, "Your username is incorrect, please enter again!", Toast.LENGTH_LONG).show();          //if detect long press on product will add to basket list
            validDetails = false;
        }
    }

    /**
     * return the user name
     * @return - the user name
     */
    public String getUsername() {
        return usernameText;
    }
}