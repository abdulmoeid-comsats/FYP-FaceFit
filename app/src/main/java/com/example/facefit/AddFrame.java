package com.example.facefit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.Random;

public class AddFrame extends AppCompatActivity
{
        private static Button add;
        private static TextView image;
        private static Spinner brand,shape;
        private static EditText name,model,price,weight,color,material,type,renderID;

        private static ImageView img;
        private Uri imageUri;
        private static final int SELECTED_PIC = 1;
        private String myUrl = "";
        private StorageTask uploadTask;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_addframe);

            image=(TextView)findViewById(R.id.set_img);
            img=(ImageView)findViewById(R.id.frame_img);
            brand=(Spinner)findViewById(R.id.brand);
            shape=(Spinner)findViewById(R.id.shape);

            name=(EditText)findViewById(R.id.name);
            model=(EditText)findViewById(R.id.model);
            price=(EditText)findViewById(R.id.price);
            weight=(EditText)findViewById(R.id.weight);
            color=(EditText)findViewById(R.id.color);
            material=(EditText)findViewById(R.id.material);
            type=(EditText)findViewById(R.id.type);
            renderID=(EditText)findViewById(R.id.renderId);


            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECTED_PIC);
                }
            });
            add=(Button)findViewById(R.id.add);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageProfilePrictureRef=storage.getReference();

                    if(imageUri != null){
                        final ProgressDialog progressDialog = new ProgressDialog(AddFrame.this);
                        progressDialog.setTitle("Update Product");
                        progressDialog.setMessage("Please wait, while we are updating your product image");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        Random random = new Random();
                        int num = random.nextInt(100000);
                        final StorageReference fileRef = storageProfilePrictureRef
                                .child("img"+num+ ".jpg");

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

                                    DatabaseReference mDatabaseAd = FirebaseDatabase.getInstance().getReference().child("frames");
                                    String key=mDatabaseAd.push().getKey();

                                    Frames frames=new Frames(name.getText().toString(),model.getText().toString(),price.getText().toString(),weight.getText().toString(),
                                            color.getText().toString(),material.getText().toString(),type.getText().toString(),renderID.getText().toString(),
                                            brand.getSelectedItem().toString(),shape.getSelectedItem().toString(),myUrl);
                                    mDatabaseAd.child(key).setValue(frames);

                                    progressDialog.dismiss();
                                    Toast.makeText(AddFrame.this, "Product Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddFrame.this, "Error.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else {
                        Toast.makeText(AddFrame.this, "Image Not Selected..!.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                case SELECTED_PIC:
                    if (resultCode == RESULT_OK) {
                        imageUri = data.getData();
                        img.setImageURI(imageUri);
                    }
                    break;
                default:
                    break;
            }
        }

}
