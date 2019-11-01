package com.adgvit.teambassador;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.adgvit.teambassador.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    public static String tempEmail;
    private TextInputEditText logInEmailEditText;
    private TextInputEditText logInPasswordEditText;
    private Button logInButton;
    private TextView logInForgotPassword;
    private TextView logInSignUp;
    private ConstraintLayout loginConstraintLayout;
    private AVLoadingIndicatorView logInProgressBar;
    private String userEmail;
    public static FirebaseAuth firebaseAuth;
    private int backButtonCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logInEmailEditText = findViewById(R.id.logInEmailEditText);
        logInPasswordEditText = findViewById(R.id.logInPasswordEditText);
        logInForgotPassword = findViewById(R.id.loginForgotPassword);
        logInButton = findViewById(R.id.logInButton);
        logInSignUp = findViewById(R.id.lognSignUP);
        loginConstraintLayout = findViewById(R.id.loginConstraintLayout);
        logInProgressBar = findViewById(R.id.logInProgressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()!=null)
        {
            tempEmail= Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()).toString().replace('.','_');
            Intent intent = new Intent(LogInActivity.this, NavigationActivity.class);
            startActivity(intent);
        }
        logInButton.setOnClickListener(this);
        logInForgotPassword.setOnClickListener(this);
        logInSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.logInButton:
                String email = Objects.requireNonNull(logInEmailEditText.getText()).toString().trim();
                String password = Objects.requireNonNull(logInPasswordEditText.getText()).toString().trim();
                if (TextUtils.isEmpty(email)) {
                    logInEmailEditText.setError("Please enter your name");
                    logInEmailEditText.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    logInPasswordEditText.setError(("Please enter your password"));
                    logInPasswordEditText.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    logInPasswordEditText.setError("Please enter a password of length greater than 6");
                    logInPasswordEditText.requestFocus();
                    return;
                }
                hideUI();
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    userEmail = (logInEmailEditText.getText()).toString().trim();
                                    tempEmail = userEmail.replace('.', '_');
                                    Intent intent = new Intent(LogInActivity.this, NavigationActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    showUI();
                                    Snackbar.make(loginConstraintLayout, "Login Failed or User not available", Snackbar.LENGTH_SHORT)
                                            .setActionTextColor(Color.WHITE).show();


                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                break;
            case R.id.loginForgotPassword:
                startActivity(new Intent(LogInActivity.this, ForgotPasswordActivity.class));
                break;
            case R.id.lognSignUP:
                startActivity(new Intent(LogInActivity.this, signUp.class));
                break;
        }
    }

    public void hideUI() {
        float alpha = 0.2f;
        loginConstraintLayout.setAlpha(alpha);
        logInProgressBar.setVisibility(View.VISIBLE);
        logInButton.setEnabled(false);
    }

    public void showUI() {
        float alpha = 1.0f;
        loginConstraintLayout.setAlpha(alpha);
        logInProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            backButtonCount=0;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }
}