package com.example.facefit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    private static EditText oldPass,newPass,cnewPass;
    private static TextView tv;
    private static Button update;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);

        oldPass=(EditText)findViewById(R.id.oldpass);
        newPass=(EditText)findViewById(R.id.password);
        cnewPass=(EditText)findViewById(R.id.confirmPassword);
        tv=(TextView)findViewById(R.id.alert);

        update=(Button)findViewById(R.id.updatePass);
        update.setOnClickListener(this);


    }

    public void closeActivity(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        if (checkValidation()){
            String old = oldPass.getText().toString();
            final String cpass = cnewPass.getText().toString();
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Update Password");
            progressDialog.setMessage("Please wait, while we are updating your password information");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            AuthCredential credential = EmailAuthProvider
                    .getCredential(CurrentUser.u_email,old);

            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(cpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseAuth.getInstance().signOut();
                                            progressDialog.dismiss();
                                            startActivity(new Intent(ChangePassword.this, MainActivity.class));
                                            finish();
                                        } else {
                                            progressDialog.dismiss();
                                            tv.setText("Error...! Password Not Updated");
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                tv.setText("Error...! Old Password Incorrect.");
                            }
                        }
                    });

        }
        else {
            tv.setText("Error...Try Again! Password Didn't Match.");
        }

    }

    private boolean checkValidation() {
        // Get all edittext texts
        String old = oldPass.getText().toString();
        String pass = newPass.getText().toString();
        String cpass = cnewPass.getText().toString();

        if (old.equals("") || old.length() == 0
                || pass.equals("") || pass.length() == 0
                || cpass.equals("")
                || cpass.length() == 0) {
            return false;
        }
        else if (!cpass.equals(pass)){
            return false;
        }
        else {
            return true;
        }
    }
}
