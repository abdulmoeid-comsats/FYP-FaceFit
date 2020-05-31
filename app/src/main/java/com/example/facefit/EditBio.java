package com.example.facefit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.appevents.suggestedevents.ViewOnClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditBio extends AppCompatActivity implements View.OnClickListener {

    private static EditText fname, lname, number, address;
    private static Button submit;
    private static TextView tv;
    public String first_name, last_name, ph_number, u_address,gender,imgUrl,status;
    ArrayList<String> list=new ArrayList<String>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbio);

        fname=(EditText)findViewById(R.id.fname);
        lname=(EditText)findViewById(R.id.lname);
        number=(EditText)findViewById(R.id.number);
        address=(EditText)findViewById(R.id.address);

        tv=(TextView)findViewById(R.id.alert);

        submit=(Button) findViewById(R.id.updateBio);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()){
                    DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").child(CurrentUser.userid).child("first_name").setValue(first_name);
                    mDatabase.child("users").child(CurrentUser.userid).child("last_name").setValue(last_name);
                    mDatabase.child("users").child(CurrentUser.userid).child("phone_number").setValue(ph_number);
                    mDatabase.child("users").child(CurrentUser.userid).child("address").setValue(u_address);
                    Toast.makeText(EditBio.this,"Bio Successfully Updated!",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    tv.setText("Error...!  Fields Cannot Be Empty.");
                }
            }
        });

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUser.userid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    fname.setText(dataSnapshot.child("first_name").getValue().toString());
                    lname.setText(dataSnapshot.child("last_name").getValue().toString());
                    number.setText(dataSnapshot.child("phone_number").getValue().toString());
                    address.setText(dataSnapshot.child("address").getValue().toString());
                    gender=dataSnapshot.child("gender").getValue().toString();
                    imgUrl=dataSnapshot.child("imgUrl").getValue().toString();
                    status=dataSnapshot.child("status").getValue().toString();
                }
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

    @Override
    public void onClick(View v) {


    }
    private boolean checkValidation() {

        // Get all edittext texts
        first_name = fname.getText().toString();
        last_name = lname.getText().toString();
        ph_number = number.getText().toString();
        u_address = address.getText().toString();

        // Check if all strings are null or not
        if (first_name.equals("") || first_name.length() == 0
                || last_name.equals("") || last_name.length() == 0
                || ph_number.equals("")
                || ph_number.length() == 0 || u_address.equals("")
                || u_address.length()==0) {
            return false;
        }
        else {

            return true;
        }

    }
}
