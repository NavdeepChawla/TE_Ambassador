package com.adgvit.teambassador;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity {

    private Pinview pin;
    private FirebaseAuth fireBaseAuth;
    private DatabaseReference dataBaseReference;
    private String codeEnter;
    private com.adgvit.teambassador.userInfo userInfo;
    String otpNumber;
    int minute, second;
    private AVLoadingIndicatorView avi;
    private String verificationID;;
    private CoordinatorLayout view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        userInfo = intent.getParcelableExtra("userInfo");

        fireBaseAuth = FirebaseAuth.getInstance();
        dataBaseReference = FirebaseDatabase.getInstance().getReference();

        TextView otpNumberTextView = findViewById(R.id.otpNumberTextView);
        final TextView countDownTextView = findViewById(R.id.countDownTextView);
        TextView otpVerifyButton = findViewById(R.id.otpVerifyButton);
        avi = findViewById(R.id.avi);
        view = findViewById(R.id.layoutOtp);

        otpNumber = "We have sent OTP to " + userInfo.getphone();
        otpNumberTextView.setText(otpNumber);

        sendVerificationCode(userInfo.getphone());

        pin = findViewById(R.id.otpPinView);

        pin.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                codeEnter = pin.getValue();
            }
        });

        otpVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode(codeEnter);
                avi.smoothToShow();
                closeKeyboard();
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
        countDownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(countDownTextView.getText().toString().trim().equals("Resend OTP"))
                {
                    sendVerificationCode(userInfo.getphone());
                    new CountDownTimer(120000,1000)
                    {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            minute = (int)millisUntilFinished/60000;
                            second = (int)(millisUntilFinished/1000)%60;

                            countDownTextView.setText("Resend OTP in " + String.format("%02d",minute) + ":" + String.format("%02d",second));
                        }

                        @Override
                        public void onFinish() {
                            countDownTextView.setText("Resend OTP");
                        }
                    }.start();
                }

            }
        });

        new CountDownTimer(120000,1000)
        {

            @Override
            public void onTick(long millisUntilFinished) {
                minute = (int)millisUntilFinished/60000;
                second = (int)(millisUntilFinished/1000)%60;

                countDownTextView.setText("Resend OTP in " + String.format("%02d",minute) + ":" + String.format("%02d",second));
            }

            @Override
            public void onFinish() {
                countDownTextView.setText("Resend OTP");
            }
        }.start();

    }


    private void sendVerificationCode(String mobileNumber)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNumber,
                30,
                TimeUnit.SECONDS,
                this,
                callback
        );
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks
            callback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationID = s;

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String verificationCode = phoneAuthCredential.getSmsCode();

            if(verificationCode != null)
            {
//                pin.setText(verificationCode);
                verifyCode(verificationCode);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.i("INFO",e.toString());
            Snackbar.make(view,e.toString(),Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.WHITE).show();
        }
    };

    private void verifyCode(String code)
    {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,code);
            signInWithCredential(credential);
        }catch (Exception e)
        {

        }

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        fireBaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            pushToDatabase(userInfo);
                            signInUserEmail(userInfo.getemail(),userInfo.getpassword());
                        }
                        else
                        {
                            Snackbar.make(view,"OTP Verification Failed",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.WHITE).show();
                            avi.smoothToHide();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }
                });
    }

    private void pushToDatabase(com.adgvit.teambassador.userInfo userInfo)
    {
        try {
            String tempEmail=userInfo.getemail().replace('.','_');
            dataBaseReference.child("Users")
                    .child(tempEmail)
                    .child("DOB").setValue(userInfo.getdob());
            dataBaseReference.child("Users")
                    .child(tempEmail)
                    .child("Email").setValue(userInfo.getemail());
            dataBaseReference.child("Users")
                    .child(tempEmail)
                    .child("Name").setValue(userInfo.getname());
            dataBaseReference.child("Users")
                    .child(tempEmail)
                    .child("PhoneNo").setValue(userInfo.getphone());
            dataBaseReference.child("Users")
                    .child(tempEmail)
                    .child("Progress").setValue(0);
            dataBaseReference.child("Users")
                    .child(tempEmail)
                    .child("Level").setValue(1);
            Log.i("INFO",tempEmail);
            Log.i("INFO",userInfo.getphone());
            Log.i("INFO",userInfo.getname());
        }
        catch (Exception e){
            Snackbar.make(view,"Database access failed",Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.WHITE).show();
        }
    }
    private void signInUserEmail(String email, String password)
    {
        fireBaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            avi.smoothToHide();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            Intent intent = new Intent(OTP.this, NavigationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        } else {

                            Snackbar.make(view,"SignUp failed",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.WHITE).show();
                            avi.smoothToHide();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        }
                    }
                });
    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}