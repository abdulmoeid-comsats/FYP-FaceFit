package com.example.facefit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import coopon.manimaran.aboutusactivity.AboutActivityBuilder;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Intent intent=getIntent();
    }
    public void closeActivity(View v){
        finish();
    }
}
