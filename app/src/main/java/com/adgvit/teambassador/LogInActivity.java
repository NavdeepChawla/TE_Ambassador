package com.adgvit.teambassador;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText logInEmailEditText;
    TextInputEditText logInPasswordEditText;
    Button logInButton;
    TextView logInForgotPassword;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logInEmailEditText = findViewById(R.id.logInEmailEditText);
        logInPasswordEditText = findViewById(R.id.logInPasswordEditText);
        logInForgotPassword = findViewById(R.id.loginForgotPassword);
        logInButton = findViewById(R.id.logInButton);

        firebaseAuth = FirebaseAuth.getInstance();

        logInButton.setOnClickListener(this);
        logInForgotPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.logInButton:

                String email = Objects.requireNonNull(logInEmailEditText.getText()).toString().trim();
                String password = Objects.requireNonNull(logInPasswordEditText.getText()).toString().trim();

                if (TextUtils.isEmpty(email)) {

                    Toast.makeText(LogInActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LogInActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(LogInActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                } else {

                                    Toast.makeText(LogInActivity.this, "Login Failed or User not available", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                break;

            case R.id.loginForgotPassword:

                startActivity(new Intent(LogInActivity.this, ForgotPasswordActivity.class));

                break;

        }
    }
}
