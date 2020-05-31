package com.example.facefit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ImageViewer extends AppCompatActivity {

    private static ImageView img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        img=(ImageView)findViewById(R.id.imgExp);

        Picasso.get().load(CurrentUser.imgUrl).into(img);
    }

    public void closeActivity(View view) {
        finish();
    }
}
