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
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
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

import java.util.Arrays;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "RegisterActivity";
    private EditText mUsername;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mPhoneNumber;

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

        //Get database instance
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference().child("users");
    }

    private void createUser() {
        Log.d(TAG, "createAccount:");
        if (!validateForm()) {
            return;
        }

        //Retrieve form data
        String username = mUsername.toString();
        String firstname = mFirstName.toString();
        String lastname = mLastName.toString();
        String email = mEmail.toString();
        String password = mPassword.toString();
        String phoneNumber = mPhoneNumber.toString();

        UserInformation newUser = new UserInformation(username, firstname, lastname, email, phoneNumber, password);

        mRef.push().setValue(newUser);
        // Read from the database
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Toast.makeText(RegisterActivity.this, "Account has been created",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Value is: " + value);

                //Redirect user to sign in
                signin();
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

    /**
     * Use firebase UI to sign user into the Application
     */
    public void signin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setLogo(R.drawable.logo)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * Method to call when sign in intent return result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            //Successful sigin
            if (resultCode == RESULT_OK) {
                Intent postIntent = new Intent(this, PostActivity.class);
                startActivity(postIntent);
            } else {
                // Sign in failed
                if (response == null) {
                    Toast.makeText(RegisterActivity.this, "Signin Cancelled",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(RegisterActivity.this, "Network Error",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(RegisterActivity.this, "Unknown error Occurred",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signin();
        } else if (i == R.id.sign_up_button) {
            Toast.makeText(RegisterActivity.this, "Updating User profile",
                    Toast.LENGTH_SHORT).show();
            createUser();
        }/* else if (i == 10) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            Toast.makeText(RegisterActivity.this, "User signed out.",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(RegisterActivity.this, PostActivity.class);
                            startActivity(new Intent(i));
                            finish();
                        }
                    });
        }*/ else {

        }
    }

    /**
     * Validate form data
     * @return
     */
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
        } else {
            mPhoneNumber = null;
        }
        return valid;
    }
}
