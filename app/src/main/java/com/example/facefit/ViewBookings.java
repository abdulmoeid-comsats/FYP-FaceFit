package com.example.facefit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class ViewBookings extends AppCompatActivity {

    private static ListView lView;
    private static CustomList lAdapter;
    private static final String TAG ="" ;
    final ArrayList<String> names = new ArrayList<String>();
    final ArrayList<String> status = new ArrayList<String>();
    final ArrayList<String> price = new ArrayList<String>();
    final ArrayList<String> date = new ArrayList<String>();
    final ArrayList<String> count = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookinglist);

        lView = (ListView) findViewById(R.id.bookingList);


        viewitems();

    }
public void viewitems(){
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("bookings").child(CurrentUser.userid);
    ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot dataSnapshotCh : dataSnapshot.getChildren()) {
                Booking booking=dataSnapshotCh.getValue(Booking.class);
                names.add(booking.frame_name);
                price.add(booking.bill_amount);
                date.add(booking.order_date);
                count.add(booking.frame_count);
                status.add(booking.status);
            }
            lAdapter = new CustomList(ViewBookings.this,names,price,status,date,count);
            lView.setAdapter(lAdapter);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    mDatabase.addValueEventListener(eventListener);
}

    public void closeActivity(View view) {
        finish();
    }
}
