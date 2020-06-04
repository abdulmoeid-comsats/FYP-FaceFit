package com.example.facefit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewExperiences extends AppCompatActivity {

    private static GridView gView;
    private static ImageAdapter lAdapter;
    private static final String TAG ="" ;
    final ArrayList<String> images = new ArrayList<String>();
    final ArrayList<String> imageid = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiences);

        gView = (GridView) findViewById(R.id.expGrid);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("experiences").child(CurrentUser.userid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshotCh : dataSnapshot.getChildren()) {
                        images.add(dataSnapshotCh.child("imgUrl").getValue().toString());
                        imageid.add(dataSnapshotCh.getKey());

                }
                lAdapter = new ImageAdapter(ViewExperiences.this,images);
                gView.setAdapter(lAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(eventListener);


        gView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
              //  Toast.makeText(ViewExperiences.this,imageid.get(position),Toast.LENGTH_SHORT).show();
                CurrentUser.imgUrl=images.get(position);
                CurrentUser.imgID=imageid.get(position);
                CurrentUser.imgViewType="";
                startActivity(new Intent(ViewExperiences.this,ImageViewer.class));
                finish();
            }
        });


    }
    public void closeActivity(View view) {
        startActivity(new Intent(ViewExperiences.this,DashboardActivity.class));
        finish();
    }
}
