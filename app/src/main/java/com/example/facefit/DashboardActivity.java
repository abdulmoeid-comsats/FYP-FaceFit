package com.example.facefit;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.appevents.suggestedevents.ViewOnClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import coopon.manimaran.aboutusactivity.AboutActivityBuilder;
import de.hdodenhof.circleimageview.CircleImageView;


public class DashboardActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {
    private static ImageView bck_img,glass_img;
    private static LinearLayout splash,home,menu;
    private static Animation fromBottom;

    private static Button add;

    private static CardView armani,dolce,rayban,nike,police,prada,tom;

    private static CircleImageView profileimg;
    private static TextView username,user_email,viewFrame,arFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bck_img=(ImageView)findViewById(R.id.background_img);
        glass_img=(ImageView)findViewById(R.id.glasses);
        splash=(LinearLayout)findViewById(R.id.splash_text);
        home=(LinearLayout)findViewById(R.id.home_text);
        menu=(LinearLayout)findViewById(R.id.menu);


        fromBottom= AnimationUtils.loadAnimation(this,R.anim.frombottom);

        bck_img.animate().translationY(-1500).setDuration(800).setStartDelay(300);
        glass_img.animate().alpha(0).setDuration(800).setStartDelay(300);
        splash.animate().translationY(140).alpha(0).setDuration(800).setStartDelay(300);
        home.startAnimation(fromBottom);
        menu.startAnimation(fromBottom);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        armani=(CardView)findViewById(R.id.armani);
        dolce=(CardView)findViewById(R.id.dolce);
        rayban=(CardView)findViewById(R.id.rayban);
        tom=(CardView)findViewById(R.id.tom);
        nike=(CardView)findViewById(R.id.nike);
        police=(CardView)findViewById(R.id.police);
        prada=(CardView)findViewById(R.id.prada);
        armani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUser.frameSelectedCategory="Armani";
                startActivity(new Intent(DashboardActivity.this,ViewFrames.class));
            }
        });
        dolce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUser.frameSelectedCategory="Dolce and Gabbana";
                startActivity(new Intent(DashboardActivity.this,ViewFrames.class));
            }
        });
        rayban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUser.frameSelectedCategory="Rayban";
                startActivity(new Intent(DashboardActivity.this,ViewFrames.class));
            }
        });
        tom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUser.frameSelectedCategory="Tom Ford";
                startActivity(new Intent(DashboardActivity.this,ViewFrames.class));
            }
        });
        nike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUser.frameSelectedCategory="Nike";
                startActivity(new Intent(DashboardActivity.this,ViewFrames.class));
            }
        });
        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUser.frameSelectedCategory="Police";
                startActivity(new Intent(DashboardActivity.this,ViewFrames.class));
            }
        });
        prada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUser.frameSelectedCategory="Prada";
                startActivity(new Intent(DashboardActivity.this,ViewFrames.class));
            }
        });

        add=(Button)findViewById(R.id.addFrame);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this,AddFrame.class));
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final LinearLayout holder=findViewById(R.id.holder);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                float scaleFactor = 7f;
                float slideX = drawerView.getWidth() * slideOffset;

                holder.setTranslationX(slideX);
                holder.setScaleX(1 - (slideOffset / scaleFactor));
                holder.setScaleY(1 - (slideOffset / scaleFactor));

                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);// will remove all possible our aactivity's window bounds
        }

        drawer.addDrawerListener(toggle);

        drawer.setScrimColor(Color.TRANSPARENT);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header= navigationView.getHeaderView(0);

        username=header.findViewById(R.id.uname);
        user_email=header.findViewById(R.id.uemail);
        profileimg=header.findViewById(R.id.profile_pic);


        user_email.setText(CurrentUser.u_email);

        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUser.userid);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("imgUrl").exists())
                    {
                        String image = dataSnapshot.child("imgUrl").getValue().toString();
                        String fname = dataSnapshot.child("first_name").getValue().toString();
                        String lname = dataSnapshot.child("last_name").getValue().toString();

                        Picasso.get().load(image).into(profileimg);
                        username.setText(fname+" "+lname);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Do you want to Logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   FirebaseAuth.getInstance().signOut();
                    finish();
                    startActivity(new Intent(DashboardActivity.this,MainActivity.class));
                }
            });
            builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert=builder.create();
            alert.show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_exp) {
            startActivity(new Intent(DashboardActivity.this,ViewExperiences.class));
        } else if (id == R.id.nav_book) {
            startActivity(new Intent(DashboardActivity.this,ViewBookings.class));

        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(DashboardActivity.this,EditProfile.class));

        } else if (id == R.id.nav_about) {

            startActivity(new Intent(DashboardActivity.this,AboutUs.class));

        } else if (id == R.id.nav_exit) {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
