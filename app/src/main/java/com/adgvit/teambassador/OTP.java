package com.adgvit.teambassador;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.wang.avi.AVLoadingIndicatorView;

import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity {

    private Pinview pin;
    private FirebaseAuth fireBaseAuth;
    private DatabaseReference dataBaseReference;
    private String codeSent, codeEnter;
    private com.adgvit.teambassador.userInfo userInfo;
    String otpNumber;
    int minute, second;
    private AVLoadingIndicatorView avi;
    private String VerificationSms;

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
                verifySignInCode(codeEnter);
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

            VerificationSms = phoneAuthCredential.getSmsCode();

            if(VerificationSms!=null){

                pin.setValue(VerificationSms);
                verifySignInCode(VerificationSms);
                avi.smoothToShow();
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OTP.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
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

                            pushToDatabase(userInfo);
                            signInUserEmail(userInfo.getemail(),userInfo.getpassword());

                        } else {

                            Toast.makeText(OTP.this, "OTP Verification Failed", Toast.LENGTH_SHORT).show();
                            avi.smoothToHide();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            }
                        }}
                );
    }
    private void pushToDatabase(com.adgvit.teambassador.userInfo userInfo)
    {
        try {
            String tempEmail=userInfo.getemail();
            char [] tempArr=tempEmail.toCharArray();
            for (int i = 0; i <tempArr.length ; i++) {
                if(tempArr[i]=='.')
                {
                    tempArr[i]='_';
                }
            }
            tempEmail=tempArr.toString();
            dataBaseReference.child("Tasks").child(tempEmail).child("Level").setValue(1);
            dataBaseReference.child("Tasks").child(tempEmail).child("Progress").setValue(0);
            dataBaseReference.child("Users")
                    .child(tempEmail)
                    .child("DOB").setValue(userInfo.getdob());
            dataBaseReference.child("Users")
                    .child(tempEmail)
                    .child("Email").setValue(userInfo.getemail());
            dataBaseReference.child("Users")
                    .child(tempEmail)
                    .child("Name").setValue(userInfo.getname());
        }
        catch (Exception e){
            Toast.makeText(this, "SignUp Failed", Toast.LENGTH_SHORT).show();
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

                            Intent intent = new Intent(OTP.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        } else {

                            Toast.makeText(OTP.this, "SignUp Failed", Toast.LENGTH_SHORT).show();
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



