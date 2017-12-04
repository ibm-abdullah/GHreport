package com.braimahabdullah.ghreport;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    private EditText mUsername;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mPhoneNumber;

    private FirebaseAuth mAuth;
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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mPhoneNumber.setText(currentUser.getPhoneNumber().toString());

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance("https://ghanareport-8f09e.firebaseio.com/");
        mRef = mFirebaseDatabase.getReferenceFromUrl("https://ghanareport-8f09e.firebaseio.com/");
    }

    private void createUser() {
        Log.d(TAG, "createAccount:");
       if (!validateForm()) {
            return;
        }
        String username = mUsername.toString();
        String firstname = mFirstName.toString();
        String lastname = mLastName.toString();
        String email = mEmail.toString();
        String password = mPassword.toString();
        String phoneNumber = mPhoneNumber.toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        //Check if a user is signed in
        if(auth.getCurrentUser() != null){

            FirebaseUser user = auth.getCurrentUser();
            String uid = user.getUid();
            //Create user info object
            UserInformation newUser = new UserInformation(username, firstname, lastname, email, phoneNumber, password);

            DatabaseReference userRef = mRef.child("users").child(uid);
            userRef.setValue(newUser);
            // Read from the database
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);
                    Toast.makeText(RegisterActivity.this, "User profile has been updated",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Value is: " + value);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(RegisterActivity.this, "Failed to update user profile",
                            Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            Intent registerIntent = new Intent(this, LoginActivity.class);
            startActivity(registerIntent);
        } else if (i == R.id.sign_up_button) {
            Toast.makeText(RegisterActivity.this, "Updating User profile",
                    Toast.LENGTH_SHORT).show();
            createUser();
        }else if(i ==R.id.sign_out_button){
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            Toast.makeText(RegisterActivity.this, "User signed out.",
                                    Toast.LENGTH_SHORT).show();
                            Intent i  = new Intent(RegisterActivity.this,PostActivity.class);
                            startActivity(new Intent(i));
                            finish();
                        }
                    });
        }
        else {

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
