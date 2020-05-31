package com.example.facefit;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPassword_Fragment extends Fragment implements
        OnClickListener {
	private static final String TAG = "ForgotPasswordActivity";
	private static View view;
	private FirebaseAuth mAuth;
	private ProgressDialog mProgressDialog;
	private FirebaseAuth.AuthStateListener mAuthListener;
	private static EditText emailId;
	private static TextView submit, back,frgottxt;

	public ForgotPassword_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.forgotpassword_layout, container,
				false);
		initViews();
		setListeners();
		mAuth = FirebaseAuth.getInstance();
		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
				} else {
					Log.d(TAG, "onAuthStateChanged:signed_out");
				}
			}};

		return view;
	}
	@Override
	public void onStart() {
		super.onStart();
		mAuth.addAuthStateListener(mAuthListener);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mAuthListener != null) {
			mAuth.removeAuthStateListener(mAuthListener);
		}
	}
	// Initialize the views
	private void initViews() {
		emailId = (EditText) view.findViewById(R.id.registered_emailid);
		submit = (TextView) view.findViewById(R.id.forgot_button);
		back = (TextView) view.findViewById(R.id.backToLoginBtn);
		frgottxt = (TextView) view.findViewById(R.id.frgtpwd);

		// Setting text selector over textviews
		XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			back.setTextColor(csl);
			submit.setTextColor(csl);

		} catch (Exception e) {
		}

	}

	// Set Listeners over buttons
	private void setListeners() {
		back.setOnClickListener(this);
		submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backToLoginBtn:

			// Replace Login Fragment on Back Presses
			new MainActivity().replaceLoginFragment();
			break;

		case R.id.forgot_button:
			if(submitButtonTask()){
				sendPasswordReset();
			}
			break;

		}

	}
	private void sendPasswordReset() {
		showProgressDialog();
		mAuth.sendPasswordResetEmail(emailId.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful()) {
					frgottxt.setTextColor(Color.DKGRAY);
					frgottxt.setText("Email sent.");
				} else {
					frgottxt.setTextColor(Color.RED);
					frgottxt.setText(task.getException().getMessage());
				}
				hideProgressDialog();
			}
		});
	}
	public void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this.getActivity());
			mProgressDialog.setMessage(getString(R.string.loading));
			mProgressDialog.setIndeterminate(true);
		}
		mProgressDialog.show();
	}

	public void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
	private boolean submitButtonTask() {
		String getEmailId = emailId.getText().toString();

		// Pattern for email id validation
		Pattern p = Pattern.compile(Utils.regEx);

		// Match the pattern
		Matcher m = p.matcher(getEmailId);

		// First check if email id is not null else show error toast
		if (getEmailId.equals("") || getEmailId.length() == 0) {

			new CustomToast().Show_Toast(getActivity(), view,
					"Please enter your Email Id.");
			return false;
		}
		// Check if email id is valid or not
		else if (!m.find()) {
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");
			return false;
		}
		// Else submit email id and fetch passwod or do your stuff
		else {
			Toast.makeText(getActivity(), "Get Forgot Password.",
					Toast.LENGTH_SHORT).show();
			return true;
		}
	}
}