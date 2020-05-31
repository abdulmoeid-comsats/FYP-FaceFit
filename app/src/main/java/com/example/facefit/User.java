package com.example.facefit;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class User {
    public String email;
    public String first_name;
    public String last_name;
    public String phone_number;
    public String address;
    public String gender;
    public String status;
    public String imgUrl;
    public ArrayList<String> preferences = new ArrayList<String>();


    public User(String email, String first_name, String last_name, String phone_number, String address,String gender,String imgUrl, ArrayList<String>  preferences) {
        this.email=email;
        this.first_name=first_name;
        this.last_name=last_name;
        this.phone_number=phone_number;
        this.address=address;
        this.gender=gender;
        this.status="enable";
        this.imgUrl=imgUrl;
        this.preferences=preferences;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public ArrayList<String>  getPreferences() {
        return preferences;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setPreferences(ArrayList<String>  preferences) {
        this.preferences = preferences;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
