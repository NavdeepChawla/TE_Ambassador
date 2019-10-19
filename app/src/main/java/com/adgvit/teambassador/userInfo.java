package com.adgvit.teambassador;

import android.os.Parcel;
import android.os.Parcelable;

public class userInfo implements Parcelable {

        private String phone;
        private String name;
        private String email;
        private String password;
        private String dob;

        public userInfo() {

        }
        public userInfo(String phone, String name, String email, String password, String dob) {

            this.phone = phone;
            this.name = name;
            this.email = email;
            this.password = password;
            this.dob = dob;


        }

    protected userInfo(Parcel in) {
        phone = in.readString();
        name = in.readString();
        email = in.readString();
        password = in.readString();
        dob = in.readString();
    }

    public static final Creator<userInfo> CREATOR = new Creator<userInfo>() {
        @Override
        public userInfo createFromParcel(Parcel in) {
            return new userInfo(in);
        }

        @Override
        public userInfo[] newArray(int size) {
            return new userInfo[size];
        }
    };

        public String getphone(){
            return phone;
        }
        public String getname(){
            return name;
        }
        public String getemail(){
            return email;
        }
        public String getpassword(){
            return password;
        }
        public String getdob(){
            return dob;
        }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phone);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(dob);
    }
}

