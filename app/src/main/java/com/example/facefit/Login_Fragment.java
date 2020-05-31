package com.example.facefit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;

import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.ProgressDialog;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login_Fragment extends Fragment implements OnClickListener {
	private static final String TAG = "LoginActivity";
	private ProgressDialog mProgressDialog;
	private static View view;
	private FirebaseAuth mAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;
	private static EditText emailid, password;
	private static Button loginButton;
	private static TextView forgotPassword, signUp, logText;
	private static CheckBox show_hide_password;
	private static LinearLayout loginLayout;
	private static Animation shakeAnimation;
	private static FragmentManager fragmentManager;

	public Login_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.login_layout, container, false);
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
	// Initiate Views
	private void initViews() {
		fragmentManager = getActivity().getSupportFragmentManager();

		emailid = (EditText) view.findViewById(R.id.login_emailid);
		password = (EditText) view.findViewById(R.id.login_password);
		loginButton = (Button) view.findViewById(R.id.loginBtn);
		forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
		signUp = (TextView) view.findViewById(R.id.createAccount);
		logText = (TextView) view.findViewById(R.id.lverify);
		show_hide_password = (CheckBox) view
				.findViewById(R.id.show_hide_password);
		loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

		// Load ShakeAnimation
		shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shake);

		// Setting text selector over textviews
		XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			forgotPassword.setTextColor(csl);
			show_hide_password.setTextColor(csl);
			signUp.setTextColor(csl);
		} catch (Exception e) {
		}
	}

	// Set Listeners
	private void setListeners() {
		loginButton.setOnClickListener(this);
		forgotPassword.setOnClickListener(this);
		signUp.setOnClickListener(this);
		// Set check listener over checkbox for showing and hiding password
		show_hide_password
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

						// If it is checkec then show password else hide
						// password
						if (isChecked) {

							show_hide_password.setText(R.string.hide_pwd);// change
																			// checkbox
																			// text

							password.setInputType(InputType.TYPE_CLASS_TEXT);
							password.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());// show password
						} else {
							show_hide_password.setText(R.string.show_pwd);// change
																			// checkbox
																			// text

							password.setInputType(InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							password.setTransformationMethod(PasswordTransformationMethod
									.getInstance());// hide password

						}

					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.loginBtn:
				if(checkValidation()){
					signIn(emailid.getText().toString(),password.getText().toString());
				}
				break;

			case R.id.forgot_password:

				// Replace forgot password fragment with animation
				fragmentManager
						.beginTransaction()
						.setCustomAnimations(R.anim.right_enter, R.anim.left_out)
						.replace(R.id.frameContainer,
								new ForgotPassword_Fragment(),
								Utils.ForgotPassword_Fragment).commit();
				break;

			case R.id.createAccount:

				// Replace signup frgament with animation
				fragmentManager
						.beginTransaction()
						.setCustomAnimations(R.anim.right_enter, R.anim.left_out)
						.replace(R.id.frameContainer, new SignUp_Fragment(),
								Utils.SignUp_Fragment).commit();
				break;

		}

	}

	// Check Validation before login
	private boolean checkValidation() {
		// Get email id and password
		String getEmailId = emailid.getText().toString();
		String getPassword = password.getText().toString();

		// Check patter for email id
		Pattern p = Pattern.compile(Utils.regEx);

		Matcher m = p.matcher(getEmailId);

		// Check for both field is empty or not
		if (getEmailId.equals("") || getEmailId.length() == 0
				|| getPassword.equals("") || getPassword.length() == 0) {
			loginLayout.startAnimation(shakeAnimation);
			new CustomToast().Show_Toast(getActivity(), view,
					"Enter both credentials.");
			return false;

		}
		// Check if email id is valid or not
		else if (!m.find()) {
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");
			return false;
		}
		// Else do login and do your stuff
		else {
			return true;
		}
	}

	private void signIn(final String email, String password) {
		final String[] data = {""};
		showProgressDialog();
		mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
				if (!task.isSuccessful()) {
					hideProgressDialog();
					new CustomToast().Show_Toast(getActivity(), view,
							"Invalid credentials.");
				} else {
					CurrentUser.u_email=email;
                    String [] userid = email.toString().split("@");
                    CurrentUser.userid=userid[0];
                    final String status="enable";
					DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUser.userid).child("status");
                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()) {
                            	Intent i = new Intent(getActivity(), ProfileActivity.class);
                            	startActivity(i);
							}
							else {
								Intent intent = new Intent(getActivity(), DashboardActivity.class);
								startActivity(intent);
							}
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
							hideProgressDialog();
                        }
                    };
                    mDatabase.addValueEventListener(eventListener);
				}

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
}
