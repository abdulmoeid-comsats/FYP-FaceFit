package com.example.facefit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Suggestions extends AppCompatActivity {

    private static GridView gView;
    private static SuggestionAdapter lAdapter;
    private static final String TAG ="" ;
    final ArrayList<String> images = new ArrayList<String>();
    final ArrayList<String> imageid = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);
        gView = (GridView) findViewById(R.id.suggGrid);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("suggestions").child(CurrentUser.frameSelected);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshotCh : dataSnapshot.getChildren()) {
                    images.add(dataSnapshotCh.child("imgUrl").getValue().toString());
                    imageid.add(dataSnapshotCh.getKey());

                }
                lAdapter = new SuggestionAdapter(Suggestions.this,images);
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
                CurrentUser.imgViewType="suggestions";
                //  Toast.makeText(ViewExperiences.this, CurrentUser.imgUrl,Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Suggestions.this,ImageViewer.class));
            }
        });


    }
    public void closeActivity(View view) {
        finish();
    }
}
