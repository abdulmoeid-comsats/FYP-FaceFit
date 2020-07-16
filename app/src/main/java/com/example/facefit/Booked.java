package com.example.facefit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Booked extends AppCompatActivity {
    private static Animation frombottom;
    private static LinearLayout mylayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked);

        mylayout=(LinearLayout)findViewById(R.id.layout_book);
        frombottom= AnimationUtils.loadAnimation(this,R.anim.frombottom);
        mylayout.startAnimation(frombottom);
    }

    public void gotoFrames(View view) {
        startActivity(new Intent(Booked.this,DashboardActivity.class));
    }
}
