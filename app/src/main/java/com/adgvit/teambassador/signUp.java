package com.adgvit.teambassador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class signUp extends AppCompatActivity {

    private EditText phoneEditText,nameEditText,emailEditText,passwordEditText,dobEditText;
    private String phone,name,email,password,dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        phoneEditText  = (EditText) findViewById(R.id.signUpPhoneEditText);
        nameEditText  = (EditText) findViewById(R.id.signUpNameEditText);
        emailEditText  = (EditText) findViewById(R.id.signUpEmailEditText);
        passwordEditText = (EditText) findViewById(R.id.signUpPasswordEditText);
        dobEditText  = (EditText) findViewById(R.id.signUpDobEditText);


        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = phoneEditText.getText().toString().trim();
                name = nameEditText.getText().toString().trim();
                email = emailEditText.getText().toString().trim();
                dob = dobEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();

                if(phone.isEmpty() || phone.length()!=10) {

                    if(phone.length()==0){

                        phoneEditText.setError("Please enter your Phone No.");
                        phoneEditText.requestFocus();

                    }else{

                        phoneEditText.setError("Please enter a Valid Phone No.");
                        phoneEditText.requestFocus();
                    }

                }
                else if(name.isEmpty()) {

                    nameEditText.setError("Please enter your name");
                    nameEditText.requestFocus();

                }
                else if(email.isEmpty()) {

                    emailEditText.setError("Please enter your email");
                    emailEditText.requestFocus();

                }
                else if(password.isEmpty()) {

                    passwordEditText.setError("please enter a password");
                    passwordEditText.requestFocus();

                }
                else if(dob.isEmpty()) {

                    dobEditText.setError("please enter your date of birth");
                    dobEditText.requestFocus();
                }
                else {
                    userInfo user = new userInfo(
                            phone,
                            name,
                            email,
                            password,
                            dob
                    );

                    Intent intent = new Intent(signUp.this,OTP.class);
                    intent.putExtra("userInfo",user);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

    }


    public void loginActivity(View view) {

        /*Intent intent = new Intent(signUp.this,login.class;)
        startActivity(intent);*/
    }
}
