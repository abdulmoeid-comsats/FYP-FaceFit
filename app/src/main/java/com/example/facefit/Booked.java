package com.example.facefit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Booked extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked);
    }

    public void gotoFrames(View view) {
        startActivity(new Intent(Booked.this,DashboardActivity.class));
    }
}
