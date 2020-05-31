package com.example.facefit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    private static CircleImageView profileimg;
    private static String image;
    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        profileimg=(CircleImageView) findViewById(R.id.profile_img);
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUser.userid);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("imgUrl").exists())
                    {
                        image = dataSnapshot.child("imgUrl").getValue().toString();
                        Picasso.get().load(image).into(profileimg);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileimg.setImageURI(imageUri);
            saveImage();
        }
        else
        {
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setProfile(View v){
        CropImage.activity(imageUri)
                .setAspectRatio(1, 1)
                .start(EditProfile.this);
    }
    public void saveImage(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageProfilePrictureRef=storage.getReference();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile Image");
        progressDialog.setMessage("Please wait, while we are updating your Profile Image");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            Random random=new Random();
            int num=random.nextInt(1000000);
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(CurrentUser.userid+num+ ".jpg");

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
                        DatabaseReference UsersRef2 = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUser.userid);
                        UsersRef2.child("imgUrl").setValue(myUrl);
                        progressDialog.dismiss();

                        Toast.makeText(EditProfile.this, "Profile Image Upload Successfully.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfile.this, "Error While Uploading!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "Image is Not Selected.", Toast.LENGTH_SHORT).show();
        }
    }
    public void chnagePass(View v){
        startActivity(new Intent(EditProfile.this,ChangePassword.class));
    }
    public void editBio(View v){
        startActivity(new Intent(EditProfile.this,EditBio.class));
    }

    public void editPref(View view) {
        startActivity(new Intent(EditProfile.this,EditPreferences.class));
    }

    public void closeActivity(View view) {
        finish();
    }
}
