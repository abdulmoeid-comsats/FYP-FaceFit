package com.example.facefit;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.AugmentedFaceNode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GlassesFit extends AppCompatActivity {

    private static final String TAG = GlassesFit.class.getSimpleName();

    private static final double MIN_OPENGL_VERSION = 3.0;

    private FaceArFragment arFragment;

    private ModelRenderable faceRegionsRenderable =null;

    private ArrayList<ModelRenderable> glasses= new ArrayList();

    private ArSceneView sceneView;

    private final HashMap<AugmentedFace, AugmentedFaceNode> faceNodeMap = new HashMap<>();

    private int renderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        Intent in=getIntent();
        String id=in.getStringExtra("renderID");
        renderID=Integer.parseInt(id.trim());
        setContentView(R.layout.activity_glasses_fit);
        arFragment = (FaceArFragment) getSupportFragmentManager().findFragmentById(R.id.face_fragment);

        buildModels();

        sceneView = arFragment.getArSceneView();
        sceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);

        Scene scene = sceneView.getScene();
        scene.addOnUpdateListener(
                (FrameTime frameTime) -> {
                    if (faceRegionsRenderable == null ) {
                        return;
                    }

                    Collection<AugmentedFace> faceList =
                            sceneView.getSession().getAllTrackables(AugmentedFace.class);

                    // Make new AugmentedFaceNodes for any new faces.
                    for (AugmentedFace face : faceList) {
                        if (!faceNodeMap.containsKey(face)) {
                            AugmentedFaceNode faceNode = new AugmentedFaceNode(face);
                            faceNode.setParent(scene);

                            faceNode.setFaceRegionsRenderable(faceRegionsRenderable);
//
                            faceNodeMap.put(face, faceNode);
                        }
                        else {
                            faceNodeMap.get(face).setFaceRegionsRenderable(faceRegionsRenderable);
                        }
                    }


                    Iterator<Map.Entry<AugmentedFace, AugmentedFaceNode>> iter =
                            faceNodeMap.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<AugmentedFace, AugmentedFaceNode> entry = iter.next();
                        AugmentedFace face = entry.getKey();
                        if (face.getTrackingState() == TrackingState.STOPPED) {
                            AugmentedFaceNode faceNode = entry.getValue();
                            faceNode.setParent(null);
                            iter.remove();
                        }
                    }
                });

    }
    private String generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());

        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "DCIM/FaceFit/" + date + "_screenshot.jpg";
    }
    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

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
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }
    private void takePhoto() {
        final String filename = generateFilename();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(sceneView.getWidth(), sceneView.getHeight(),
                Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        // Make the request to copy.
        PixelCopy.request(sceneView, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename);
                } catch (IOException e) {
                    Toast toast = Toast.makeText(GlassesFit.this, e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Photo saved", Snackbar.LENGTH_LONG);
                snackbar.setAction("Open in Photos", v -> {
                    File photoFile = new File(filename);

                    Uri photoURI = FileProvider.getUriForFile(GlassesFit.this,
                            GlassesFit.this.getPackageName() + ".ar.codelab.name.provider",
                            photoFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                    intent.setDataAndType(photoURI, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);

                });
                snackbar.setAction("Upload Photo", v -> {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageProfilePrictureRef=storage.getReference();
                    final StorageReference fileRef = storageProfilePrictureRef
                            .child(filename);
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading Image");
                    progressDialog.setMessage("Please wait, while we are updating your image to our Database");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    UploadTask uploadTask = fileRef.putBytes(data);
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
                                String myUrl = downloadUrl.toString();

                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                String key=mDatabase.push().getKey();
                                mDatabase.child("experiences").child(CurrentUser.userid).child(key).child("imgUrl").setValue(myUrl);

                                String key2=mDatabase.push().getKey();
                                mDatabase.child("suggestions").child(CurrentUser.frameSelected).child(key2).child("imgUrl").setValue(myUrl);

                                progressDialog.dismiss();

                                Toast.makeText(GlassesFit.this, "Image Upload Successfully.", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(GlassesFit.this, "Error. Failed To Upload", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                });
                snackbar.show();
            } else {
                Toast toast = Toast.makeText(GlassesFit.this,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }

    private void buildModels() {

        ModelRenderable.builder()
                .setSource(GlassesFit.this, Uri.parse(CurrentUser.frames[renderID]))
                .build()
                .thenAccept(
                        modelRenderable -> {
                            glasses.add(modelRenderable);
                            faceRegionsRenderable = modelRenderable;
                            modelRenderable.setShadowCaster(false);
                            modelRenderable.setShadowReceiver(false);
                        });
    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (ArCoreApk.getInstance().checkAvailability(activity)
                == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            Log.e(TAG, "Augmented Faces requires ARCore.");
            Toast.makeText(activity, "Augmented Faces requires ARCore", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    public void closeActivity(View view) {
        finish();
    }


    public void onCapture(View view) {
        takePhoto();
    }

    public void onSuggestions(View view) {
    }
}
