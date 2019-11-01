package com.adgvit.teambassador;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    ConstraintLayout forgotPasswordConstraintLayout;
    TextInputEditText forgotPasswordEmailEditText;
    Button forgotPasswordButton;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotPasswordConstraintLayout = findViewById(R.id.forgotPasswordConstraintLayout);
        forgotPasswordEmailEditText = findViewById(R.id.forgotPasswordEmailEditText);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);

        firebaseAuth = FirebaseAuth.getInstance();

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = Objects.requireNonNull(forgotPasswordEmailEditText.getText()).toString().trim();

                if (TextUtils.isEmpty(email)) {
                    forgotPasswordEmailEditText.setError("Please enter your registered email id");
                    forgotPasswordEmailEditText.requestFocus();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Snackbar.make(forgotPasswordConstraintLayout, "Password reset email sent!", Snackbar.LENGTH_SHORT)
                                        .setActionTextColor(Color.WHITE).show();
                                finish();
                                startActivity(new Intent(ForgotPasswordActivity.this, LogInActivity.class));
                            } else {
                                Snackbar.make(forgotPasswordConstraintLayout, "Error in sending password reset email", Snackbar.LENGTH_SHORT)
                                        .setActionTextColor(Color.WHITE).show();
                            }
                        }
                    });
                }
            }
        });
    }
}