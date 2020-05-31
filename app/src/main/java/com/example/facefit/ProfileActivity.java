package com.example.facefit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";
    private ProgressDialog mProgressDialog;
    private DatabaseReference mDatabase;
    private static EditText fname, lname, number, address;
    private static TextView set_profile;
    private static Button submit;
    private static CheckBox dg, nike, armani, police, prada, rayban, tomford;
    private static RadioGroup gender_radioGroup;
    private static LinearLayout activity_profile;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;
    public ArrayList<String> list = new ArrayList<String>();
    public String email,first_name, last_name, ph_number, u_address,gender;

    private CircleImageView profileImageView;
    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;

    public ProfileActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileImageView=(CircleImageView)findViewById(R.id.profile_img);
        fname=(EditText)findViewById(R.id.fname);
        lname=(EditText)findViewById(R.id.lname);
        number=(EditText)findViewById(R.id.number);
        address=(EditText)findViewById(R.id.address);

        submit=(Button) findViewById(R.id.submit);

        gender_radioGroup= (RadioGroup)findViewById(R.id.genderRadio);

        dg=(CheckBox) findViewById(R.id.dg);
        nike=(CheckBox) findViewById(R.id.nike);
        armani=(CheckBox) findViewById(R.id.armani);
        police=(CheckBox) findViewById(R.id.police);
        prada=(CheckBox) findViewById(R.id.prada);
        rayban=(CheckBox) findViewById(R.id.rayban);
        tomford=(CheckBox) findViewById(R.id.tom);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent=getIntent();

        gender_radioGroup.clearCheck();

        gender_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton radioButton = (RadioButton)group.findViewById(checkedId);
                gender=radioButton.getText().toString();
            }
        });

        setListeners();
    }

    public void setListeners(){
        submit.setOnClickListener(this);

        dg.setOnClickListener(this);
        nike.setOnClickListener(this);
        armani.setOnClickListener(this);
        police.setOnClickListener(this);
        prada.setOnClickListener(this);
        rayban.setOnClickListener(this);
        tomford.setOnClickListener(this);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
            finish();
        }
    }

    public void setProfile(View v){
        CropImage.activity(imageUri)
                .setAspectRatio(1, 1)
                .start(ProfileActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                if (checkValidation()){
                    saveInfo();
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

    private boolean checkValidation() {

        // Get all edittext texts
        first_name = fname.getText().toString();
        last_name = lname.getText().toString();
        ph_number = number.getText().toString();
        u_address = address.getText().toString();
        int selectedId = gender_radioGroup.getCheckedRadioButtonId();

        // Check if all strings are null or not
        if (first_name.equals("") || first_name.length() == 0
                || last_name.equals("") || last_name.length() == 0
                || ph_number.equals("")
                || ph_number.length() == 0 || u_address.equals("")
                || u_address.length()==0 || selectedId == -1) {
            new CustomToast().Show_Toast(null, activity_profile,
                    "All fields are required.");
            return false;
        }
        else {

            return true;
        }

    }

    public void saveInfo(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageProfilePrictureRef=storage.getReference();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(CurrentUser.userid + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        User user=new User(CurrentUser.u_email,first_name,last_name,ph_number,u_address,gender,myUrl,list);
                        mDatabase.child("users").child(CurrentUser.userid).setValue(user);

                        progressDialog.dismiss();

                        startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
                        Toast.makeText(ProfileActivity.this, "Profile Info Upload Successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "Image is Not Selected.", Toast.LENGTH_SHORT).show();
        }
    }

}
