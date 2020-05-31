package com.example.facefit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUp_Fragment extends Fragment implements OnClickListener {
	private static final String TAG = "EmailPasswordActivity";
	private FirebaseAuth mAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;
	private static View view;
	private static EditText emailId,
			password, confirmPassword;
	private static TextView login,Verify;
	private static Button signUpButton,VButton;
	private static CheckBox terms_conditions;

	public SignUp_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.signup_layout, container, false);
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



	// Initialize all views
	private void initViews() {
		emailId = (EditText) view.findViewById(R.id.userEmailId);
		password = (EditText) view.findViewById(R.id.password);
		confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
		signUpButton = (Button) view.findViewById(R.id.signUpBtn);
		login = (TextView) view.findViewById(R.id.already_user);
		Verify = (TextView) view.findViewById(R.id.verify);
		terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);
		VButton = (Button) view.findViewById(R.id.VerifyBtn);
		VButton.setEnabled(false);
		view.findViewById(R.id.VerifyBtn).setVisibility(View.GONE);

		// Setting text selector over textviews
		XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			login.setTextColor(csl);
			terms_conditions.setTextColor(csl);
		} catch (Exception e) {
		}
	}

	// Set Listeners
	private void setListeners() {
		signUpButton.setOnClickListener(this);
		VButton.setOnClickListener(this);
		login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.signUpBtn:
				if(checkValidation()) {
					createAccount(emailId.getText().toString(), password.getText().toString());
					emailVerification();
				}
				//startActivity(new Intent(this.getActivity(),VerifyActivity.class));
				break;

			case R.id.already_user:
				// Replace login fragment
				new MainActivity().replaceLoginFragment();
				break;
			case R.id.VerifyBtn:
				// Replace login fragment
				new MainActivity().replaceLoginFragment();
				break;
		}

	}
	private ProgressDialog mProgressDialog;
	private void createAccount(String email, String password) {

//		new BaseActivity().
		showProgressDialog();
		mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if (!task.isSuccessful()) {
					Verify.setTextColor(Color.RED);
					Verify.setText(task.getException().getMessage());
				} else {
					Verify.setTextColor(Color.DKGRAY);
					final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
					final Context c = getActivity();
					firebaseUser.sendEmailVerification().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()) {
								Toast.makeText(
										c, "Verification email sent to " + firebaseUser.getEmail(), Toast.LENGTH_LONG
								).show();
							} else {
								Toast.makeText(c, task.getException().getMessage(), Toast.LENGTH_LONG).show();
							}
						}
					});
				}
		//		new BaseActivity().hideProgressDialog();
				hideProgressDialog();
			}
		});
	}
	private void emailVerification(){
		VButton.setEnabled(true);
		emailId.setEnabled(false);
		password.setEnabled(false);
		confirmPassword.setEnabled(false);
		terms_conditions.setEnabled(false);
		view.findViewById(R.id.VerifyBtn).setVisibility(View.VISIBLE);
		view.findViewById(R.id.signUpBtn).setVisibility(View.GONE);
		emailId.setText("");
		password.setText("");
		confirmPassword.setText("");
		Verify.setText("The Verification Email has been sent to your email...Please Verify then LOGIN!");
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

//	@Override
//	pu void onStop() {
//		super.onStop();
//		hideProgressDialog();
//	}


	// Check Validation Method
	private boolean checkValidation() {

		// Get all edittext texts
		String getEmailId = emailId.getText().toString();
		String getPassword = password.getText().toString();
		String getConfirmPassword = confirmPassword.getText().toString();

		// Pattern match for email id
		Pattern p = Pattern.compile(Utils.regEx);
		Matcher m = p.matcher(getEmailId);

		// Check if all strings are null or not
		if (getEmailId.equals("") || getEmailId.length() == 0
				|| getPassword.equals("") || getPassword.length() == 0
				|| getConfirmPassword.equals("")
				|| getConfirmPassword.length() == 0) {
			new CustomToast().Show_Toast(getActivity(), view,
					"All fields are required.");
			return false;
		}
			// Check if email id valid or not
		else if (!m.find()){
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");
			return false;
	}
			// Check if both password should be equal
		else if (!getConfirmPassword.equals(getPassword)){
			new CustomToast().Show_Toast(getActivity(), view,
					"Both password doesn't match.");
			return false;
	}
		// Make sure user should check Terms and Conditions checkbox
		else if (!terms_conditions.isChecked()) {
			new CustomToast().Show_Toast(getActivity(), view,
					"Please select Terms and Conditions.");
			return false;
		}
		// Else do signup or do your stuff
		else {

			return true;
		}

	}

}
