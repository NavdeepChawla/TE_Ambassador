package com.adgvit.teambassador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity {

    private Pinview pin;
    private FirebaseAuth fireBaseAuth;
    private DatabaseReference dataBaseReference;
    private String codeSent, codeEnter;
    private userInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        userInfo = intent.getParcelableExtra("userInfo");

        fireBaseAuth = FirebaseAuth.getInstance();

        sendVerificationCode(userInfo.getphone());

        pin = (Pinview) findViewById(R.id.otpPinView);

        pin.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                codeEnter = pin.getValue();
                Toast.makeText(OTP.this, codeEnter, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void sendVerificationCode(String phone) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallback
        );
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }
    };
    private void verifySignInCode(String codeEnter)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent,codeEnter);
        signInWithPhoneAuthCredential(credential);

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fireBaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userInfo.getphone())
                                    .child("DOB").push().setValue(userInfo.getdob());
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userInfo.getphone())
                                    .child("Email").push().setValue(userInfo.getemail());
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userInfo.getphone())
                                    .child("Name").push().setValue(userInfo.getname());

                            Intent intent = new Intent(OTP.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {

                            Toast.makeText(OTP.this, "Verification Failed", Toast.LENGTH_SHORT).show();

                            }
                        }}
                );}

}



