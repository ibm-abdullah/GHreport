package com.braimahabdullah.ghreport;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Register";
    private EditText mUsername;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mPhoneNumber;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mRef;
    private FirebaseDatabase mFirebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Instantiate Views
        mUsername = (EditText) findViewById(R.id.username);
        mFirstName = (EditText) findViewById(R.id.firstname);
        mLastName = (EditText) findViewById(R.id.lastname);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mConfirmPassword = (EditText) findViewById(R.id.confirm_password);
        mPhoneNumber = (EditText) findViewById(R.id.phone);

        //Buttons
        findViewById(R.id.sign_up_button).setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance("https://ghanareport-8f09e.firebaseio.com/");
    }

    private void createUser() {
        Log.d(TAG, "createAccount:");
/*        if (!validateForm()) {
            return;
        }*/
        String username = mUsername.toString();
        String firstname = mFirstName.toString();
        String lastname = mLastName.toString();
        String email = mEmail.toString();
        String password = mPassword.toString();
        String phoneNumber = mPhoneNumber.toString();

        UserInformation newUser = new UserInformation(username, firstname, lastname, email, phoneNumber, password);
        createAccount(email,password);
        newUser.setUserId(user.getUid());

        mRef.child("users").child(newUser.getUserId()).setValue(newUser);
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Register.this, "New Account created",
                                    Toast.LENGTH_SHORT).show();
                            user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            Intent registerIntent = new Intent(this, LoginActivity.class);
            startActivity(registerIntent);
        } else if (i == R.id.sign_up_button) {
            //createUser();
            Toast.makeText(Register.this, "Creating new User",
                    Toast.LENGTH_SHORT).show();
        } else {

        }
    }

    private boolean validateForm() {
        boolean valid = true;

        //Email
        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        //Password
        String password = mPassword.getText().toString();
        String confirm_password = mConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        } else if (TextUtils.isEmpty(confirm_password)) {
            mConfirmPassword.setError("Confirm Password");
            valid = false;
        } else if (!password.equals(confirm_password)) {
            mConfirmPassword.setError("Password do not match");
            valid = false;
        } else {
            mPassword.setError(null);
            mConfirmPassword.setError(null);
        }
        String username = mUsername.getText().toString();

        if (TextUtils.isEmpty(username)) {
            mUsername.setError("Required");
            valid = false;
        } else {
            mUsername.setError(null);
        }

        //Firstname
        String firstname = mFirstName.getText().toString();
        if (TextUtils.isEmpty(firstname)) {
            mFirstName.setError("Required");
            valid = false;
        } else {
            mFirstName = null;
        }

        //Lastname
        String lastname = mLastName.getText().toString();
        if (TextUtils.isEmpty(lastname)) {
            mLastName.setError("Required");
            valid = false;
        } else {
            mLastName = null;
        }

        //Phone number
        String phone_number = mPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(lastname)) {
            mPhoneNumber.setError("Required");
            valid = false;
        } else if (!TextUtils.isDigitsOnly(phone_number)) {
            mPhoneNumber.setError("Incorrect phone number");
            valid = false;
        } else {
            mPhoneNumber = null;
        }
        return valid;
    }
}
