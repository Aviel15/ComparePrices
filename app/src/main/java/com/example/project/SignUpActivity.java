package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

/**
 * activity to sign up new users, asks for username and password
 */
public class SignUpActivity extends AppCompatActivity {
    //properties
    private Button btnSignUp, btnLogin;                         //buttons to sign up and to LogInActivity
    private ImageView image;                                    //Image GIF
    private TextInputLayout username, password1, password2;
    private boolean validDetails = true;                        //check if all details that entered is valid
    private final User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //get id
        btnSignUp = findViewById(R.id.buttonSignUp2);
        btnLogin = findViewById(R.id.buttonLogIn2);
        image = findViewById(R.id.gifImageView2);
        username = findViewById(R.id.username2);
        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
    }

    /**
     * move to sign in screen transition animation
     * @param view - btn to move LogInActivity
     */
    public void btnAlreadySignUp(View view) {
        Intent i = new Intent(this, LogInActivity.class);

        Pair[] pairs = new Pair[6];                             //all views

        pairs[0] = new Pair<>(image, "logo_image");
        pairs[1] = new Pair<>(btnSignUp, "sign_up_tran");
        pairs[2] = new Pair<>(btnLogin, "button_tran");
        pairs[3] = new Pair<>(username, "username_tran");
        pairs[4] = new Pair<>(password1, "password_tran");
        pairs[5] = new Pair<>(password1, "password_tran");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, pairs);
        startActivity(i, options.toBundle());
    }

    /**
     * on click to button sign up a new account
     * @param view - button to sign up
     */
    public void btnSignup(View view)
    {
        integrityCheck();
        if(validDetails) {
            Toast.makeText(SignUpActivity.this, "Registration was successful, sign in", Toast.LENGTH_LONG).show();          //move to LogInActivity
            Intent i = new Intent(this, LogInActivity.class);
            startActivity(i);
        }
    }

    //check if all field in edit text are ok
    private void integrityCheck()
    {
        validDetails = true;
        String usernameText = Objects.requireNonNull(username.getEditText()).getText().toString().trim();               //username
        String passwordText1 = Objects.requireNonNull(password1.getEditText()).getText().toString().trim();             //password first
        String passwordText2 = Objects.requireNonNull(password2.getEditText()).getText().toString().trim();             //password confirm

        if(usernameText.isEmpty())                          //if username is not empty
        {
            username.setError("Username is required!");
            username.requestFocus();
            validDetails = false;
        }
        else
            username.setError(null);


        if(passwordText1.isEmpty())                         //if first password text is not empty
        {
            password1.setError("Password is required!");
            password1.requestFocus();
            validDetails = false;
        }
        else
            password1.setError(null);

        if(passwordText1.length() > 15)                //if first password text is not more than 15
        {
            password1.setError("Max length is 15!");
            password1.requestFocus();
            validDetails = false;
        }
        else
            password1.setError(null);

        if(!passwordText2.equals(passwordText1))             //if first password text equals to second password
        {
            password2.setError("Please enter same password!");
            password2.requestFocus();
            validDetails = false;
        }

        boolean userExist;
        if(validDetails) {
            user.setUsername(usernameText);
            userExist = user.usernameExists();                  //check if the username already exist in data base
            if (userExist) {
                Toast.makeText(this, "This username already exists in the application, please choose another!", Toast.LENGTH_LONG).show();          //if detect long press on product will add to basket list
                validDetails = false;
            }
            else {
                passwordText1 = HashingPassword.hashPassword(passwordText1);            //hash the password to deny the cyber attacks
                user.setPassword(passwordText1);
                user.setCanReport(1);
                user.insertLogIn();
            }
        }
    }
}