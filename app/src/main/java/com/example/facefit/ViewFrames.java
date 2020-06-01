package com.example.facefit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
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

public class ViewFrames extends AppCompatActivity {

    private static GridView gView;
    private static ListAdapter lAdapter;
    private static final String TAG ="" ;
    final ArrayList<String> names = new ArrayList<String>();
    final ArrayList<String> brands = new ArrayList<String>();
    final ArrayList<String> price = new ArrayList<String>();
    final ArrayList<String> imageUrl = new ArrayList<String>();
    final ArrayList<String> brandid = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_framelist);

        gView = (GridView) findViewById(R.id.framesGrid);



        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("frames");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshotCh : dataSnapshot.getChildren()) {
                    if (dataSnapshotCh.child("brand").getValue().toString().equals(CurrentUser.frameSelectedCategory)) {
                        names.add(dataSnapshotCh.child("name").getValue().toString());
                        brands.add(dataSnapshotCh.child("brand").getValue().toString());
                        price.add(dataSnapshotCh.child("price").getValue().toString());
                        imageUrl.add(dataSnapshotCh.child("imgUrl").getValue().toString());
                        brandid.add(dataSnapshotCh.getKey());
                    }

                }
                lAdapter = new ListAdapter(ViewFrames.this,names,price,brands,imageUrl);
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
               Toast.makeText(ViewFrames.this,brandid.get(position),Toast.LENGTH_SHORT).show();
               CurrentUser.frameSelected=brandid.get(position);
               startActivity(new Intent(ViewFrames.this,BookFrame.class));
            }
        });

    }


    public void closeActivity(View view) {
        finish();
    }
}
