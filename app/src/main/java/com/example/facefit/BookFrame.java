package com.example.facefit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class BookFrame extends AppCompatActivity {
    private static TextView fname,fbrandmodel,fprice,fcolor,fweight,fshape,ftype,fmaterial,quantity;
    private static LinearLayout plus,minus;
    private static ImageView fimage;
    private static TextView book,tryframe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        fname=(TextView)findViewById(R.id.nameFrameDet);
        fbrandmodel=(TextView)findViewById(R.id.brandModelFrameDet);
        fprice=(TextView)findViewById(R.id.priceFrameDet);
        fcolor=(TextView)findViewById(R.id.colorDet);
        fweight=(TextView)findViewById(R.id.weightDet);
        fshape=(TextView)findViewById(R.id.shapeDet);
        ftype=(TextView)findViewById(R.id.typeDet);
        fmaterial=(TextView)findViewById(R.id.materialDet);

        quantity=(TextView)findViewById(R.id.quantityDet);

        plus=(LinearLayout) findViewById(R.id.plus);
        minus=(LinearLayout) findViewById(R.id.minus);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quan=Integer.parseInt(quantity.getText().toString());
                if(quan > 0 && quan < 9){
                    quan=quan+1;
                    quantity.setText(quan+"");
                }
                else {
                    Toast.makeText(BookFrame.this,"Ranges Between 0 and 10",Toast.LENGTH_SHORT).show();
                }

            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quan=Integer.parseInt(quantity.getText().toString());
                if(quan > 1 && quan < 10){
                    quan=quan-1;
                    quantity.setText(quan+"");
                }
                else {
                    Toast.makeText(BookFrame.this,"Ranges Between 0 and 10",Toast.LENGTH_SHORT).show();
                }
            }
        });

        fimage=(ImageView)findViewById(R.id.imgFrameDet);

        book=(TextView) findViewById(R.id.bookNow);
        tryframe=(TextView) findViewById(R.id.tryFrame);
        tryframe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("frames").child(CurrentUser.frameSelected);
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshotCh) {

                        Frames frames=dataSnapshotCh.getValue(Frames.class);
                        Intent intent=new Intent(BookFrame.this,GlassesFit.class);
                        intent.putExtra("renderID",frames.renderID);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                };

                mDatabase.addListenerForSingleValueEvent(eventListener);
            }
        });

        final ProgressDialog progressDialog = new ProgressDialog(BookFrame.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("frames").child(CurrentUser.frameSelected);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshotCh) {

                    Frames frames=dataSnapshotCh.getValue(Frames.class);

                    fname.setText(frames.name);
                    fbrandmodel.setText(frames.brand+" "+frames.model);
                    fprice.setText(frames.price);
                    fcolor.setText("\u2022 Color: "+frames.color);
                    fweight.setText("\u2022 Weight: "+frames.weight+"g");
                    fshape.setText("\u2022 Shape: "+frames.shape);
                    ftype.setText("\u2022 Type: "+frames.type);
                    fmaterial.setText("\u2022 Material: "+frames.material);

                    Picasso.get().load(frames.imgUrl).into(fimage);
                    progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        };

        mDatabase.addListenerForSingleValueEvent(eventListener);

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(BookFrame.this);
                progressDialog.setTitle("Booking");
                progressDialog.setMessage("Please wait, while we are booking your order");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("bookings").child(CurrentUser.userid);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String orderDate = formatter.format(date);
                String name = fname.getText().toString() + " " + fbrandmodel.getText().toString();
                String price = fprice.getText().toString();
                String count = quantity.getText().toString();
                String status = "IN PROGRESS";
                float amount = Integer.parseInt(price) * Integer.parseInt(count);

                String key = mDatabase.push().getKey();
                Booking booking = new Booking(key,CurrentUser.u_email, name, orderDate, amount + "", count, status);
                mDatabase.child(key).setValue(booking);
                Toast.makeText(BookFrame.this, "Your Frame Has Been Booked", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                startActivity(new Intent(BookFrame.this, Booked.class));
            }
            });
    }
    public void closeActivity(View view) {
        finish();
    }
}
