package com.example.facefit;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EditPreferences extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG ="Preferences" ;
    private DatabaseReference mDatabase;
    private static Button update;
    private static CheckBox dg, nike, armani, police, prada, rayban, tomford;
    public ArrayList<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        dg=(CheckBox) findViewById(R.id.dg);
        nike=(CheckBox) findViewById(R.id.nike);
        armani=(CheckBox) findViewById(R.id.armani);
        police=(CheckBox) findViewById(R.id.police);
        prada=(CheckBox) findViewById(R.id.prada);
        rayban=(CheckBox) findViewById(R.id.rayban);
        tomford=(CheckBox) findViewById(R.id.tom);

        update=(Button) findViewById(R.id.updatepref);

        dg.setOnClickListener(this);
        nike.setOnClickListener(this);
        armani.setOnClickListener(this);
        police.setOnClickListener(this);
        prada.setOnClickListener(this);
        rayban.setOnClickListener(this);
        tomford.setOnClickListener(this);

        update.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void closeActivity(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.updatepref:
                if (!list.isEmpty()){
                    saveInfo();
                }
                else {
                    finish();
                }
                break;
            case R.id.dg:
                if (dg.isChecked()){
                    list.add("Dolce and Gabana");
                }
                else {
                    list.remove("Dolce and Gabana");
                }
                break;
            case R.id.nike:
                if (nike.isChecked()){
                    list.add("Nike");
                }
                else {
                    list.remove("Nike");
                }
                break;
            case R.id.armani:
                if (armani.isChecked()){
                    list.add("Armani");
                }
                else {
                    list.remove("Armani");
                }
                break;
            case R.id.police:
                if (police.isChecked()){
                    list.add("Police");
                }
                else {
                    list.remove("Police");
                }
                break;
            case R.id.prada:
                if (prada.isChecked()){
                    list.add("Prada");
                }
                else {
                    list.remove("Prada");
                }
                break;
            case R.id.rayban:
                if (rayban.isChecked()){
                    list.add("Dolce and Gabana");
                }
                else {
                    list.remove("Dolce and Gabana");
                }
                break;
            case R.id.tom:
                if (tomford.isChecked()){
                    list.add("Tom Ford");
                }
                else {
                    list.remove("Tom Ford");
                }
                break;
        }

    }

    private void saveInfo() {
        mDatabase.child("users").child(CurrentUser.userid).child("preferences").setValue(list);
        finish();
    }
}
