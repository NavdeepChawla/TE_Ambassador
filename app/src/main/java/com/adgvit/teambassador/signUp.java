package com.adgvit.teambassador;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class signUp extends AppCompatActivity {

    private TextInputEditText phoneEditText, nameEditText, emailEditText, passwordEditText, dobEditText;
    private String phone, name, email, password, dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        phoneEditText = findViewById(R.id.signUpPhoneEditText);
        nameEditText = findViewById(R.id.signUpNameEditText);
        emailEditText = findViewById(R.id.signUpEmailEditText);
        passwordEditText = findViewById(R.id.signUpPasswordEditText);
        dobEditText = findViewById(R.id.signUpDobEditText);

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dpd = new DatePickerDialog(signUp.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        String date = dayOfMonth + "/" + (monthOfYear+1) + "/" + year;
                        dobEditText.setText(date);
                        dobEditText.setSelection(dobEditText.getText().length());
                    }
                },      Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                dpd.show();

                Button ok = dpd.getButton(DatePickerDialog.BUTTON_POSITIVE);
                Button can = dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE);

                ok.setTextColor(getResources().getColor(R.color.colorBlue));
                can.setTextColor(getResources().getColor(R.color.colorBlue));

                ok.setBackground(null);
                can.setBackground(null);
            }
        });


        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = phoneEditText.getText().toString().trim();
                name = nameEditText.getText().toString().trim();
                email = emailEditText.getText().toString().trim();
                dob = dobEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();

                if (phone.isEmpty() || phone.length() < 10) {

                    if (phone.length() == 0) {

                        phoneEditText.setError("Please Enter a Phone No.");
                        phoneEditText.requestFocus();

                    } else {

                        phoneEditText.setError("Please enter a Valid Phone No.");
                        phoneEditText.requestFocus();
                    }

                } else if (name.isEmpty()) {

                    nameEditText.setError("Please enter your name");
                    nameEditText.requestFocus();

                } else if (email.isEmpty()) {

                    emailEditText.setError("Please enter your email");
                    emailEditText.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    emailEditText.setError("Please enter a valid email");
                    emailEditText.requestFocus();
                } else if (password.isEmpty() || password.length() < 6) {

                    if (password.length() == 0) {
                        passwordEditText.setError("please enter a password");
                        passwordEditText.requestFocus();
                    } else {
                        passwordEditText.setError("please enter a password of length greater than 6");
                        passwordEditText.requestFocus();
                    }

                } else if (dob.isEmpty()) {

                    dobEditText.setError("please enter your date of birth");
                    dobEditText.requestFocus();
                } else {

                    phone = "+91 " + phone;
                    userInfo user = new userInfo(
                            phone,
                            name,
                            email,
                            password,
                            dob
                    );

                    Intent intent = new Intent(signUp.this, OTP.class);
                    intent.putExtra("userInfo", user);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

    }


    public void loginActivity(View view) {

        Intent intent = new Intent(signUp.this,LogInActivity.class);
        startActivity(intent);
    }
}