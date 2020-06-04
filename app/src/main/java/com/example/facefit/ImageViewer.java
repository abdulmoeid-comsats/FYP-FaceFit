package com.example.facefit;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageViewer extends AppCompatActivity {

    private static ImageView img,download,share,delete;
    private static LinearLayout iconLayout;
    private ProgressDialog mProgressDialog;
    private AsyncTask mMyTask;
    private static Boolean isShare=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        img=(ImageView)findViewById(R.id.imgExp);
        download=(ImageView)findViewById(R.id.dowmloadImg);
        share=(ImageView)findViewById(R.id.shareImg);
        delete=(ImageView)findViewById(R.id.deleteImg);

        iconLayout=(LinearLayout)findViewById(R.id.iconLayout);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle("Waiting");
        mProgressDialog.setMessage("Please wait...");

        if (CurrentUser.imgViewType.equals("suggestions")){
            iconLayout.setVisibility(View.GONE);
        }

        Picasso.get().load(CurrentUser.imgUrl).into(img);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShare=false;
                mMyTask = new DownloadTask()
                        .execute(stringToURL(
                                CurrentUser.imgUrl
                        ));
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShare=true;
                mMyTask = new DownloadTask()
                        .execute(stringToURL(
                                CurrentUser.imgUrl
                        ));
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabase2= FirebaseDatabase.getInstance().getReference().child("experiences").child(CurrentUser.userid).child(CurrentUser.imgID);
                mDatabase2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            dataSnapshot.getRef().removeValue();
                                            startActivity(new Intent(ImageViewer.this,ViewExperiences.class));
                                            finish();
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(ImageViewer.this);
                            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private class DownloadTask extends AsyncTask<URL,Void,Bitmap>{
        // Before the tasks execution
        protected void onPreExecute(){
            // Display the progress dialog on async task start
            mProgressDialog.show();
        }

        // Do the task in background/non UI thread
        protected Bitmap doInBackground(URL...urls){
            URL url = urls[0];
            HttpURLConnection connection = null;

            try{
                // Initialize a new http url connection
                connection = (HttpURLConnection) url.openConnection();

                // Connect the http url connection
                connection.connect();

                // Get the input stream from http url connection
                InputStream inputStream = connection.getInputStream();

                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                // Return the downloaded bitmap
                return bmp;

            }catch(IOException e){
                e.printStackTrace();
            }finally{
                // Disconnect the http url connection
                connection.disconnect();
            }
            return null;
        }

        // When all async task done
        protected void onPostExecute(Bitmap result){
            // Hide the progress dialog
            mProgressDialog.dismiss();

            if(result!=null){
                if (isShare){
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "title");
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);


                    OutputStream outstream;
                    try {
                        outstream = getContentResolver().openOutputStream(uri);
                        result.compress(Bitmap.CompressFormat.PNG, 100, outstream);
                        outstream.close();
                    } catch (Exception e) {
                        System.err.println(e.toString());
                    }

                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share Image"));
                }
                else {
                    // Save bitmap to internal storage
                    Uri imageInternalUri = saveImageToInternalStorage(result);
                    Toast.makeText(ImageViewer.this, "Image Successfully Downloaded!" + imageInternalUri.toString(), Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(ImageViewer.this,"Error Occurs While Downloading!",Toast.LENGTH_SHORT).show();
            }

        }
    }

    // Custom method to convert string to url
    protected URL stringToURL(String urlString){
        try{
            URL url = new URL(urlString);
            return url;
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    // Custom method to save a bitmap into internal storage
    protected Uri saveImageToInternalStorage(Bitmap bitmap){

        String date = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());

        String filename= Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "DCIM/Downloads/" + date + "_download.jpg";

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Uri savedImageURI = Uri.parse(out.getAbsolutePath());

        return savedImageURI;
    }

    public void closeActivity(View view) {
        finish();
    }
}

