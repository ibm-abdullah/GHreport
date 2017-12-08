package com.braimahabdullah.ghreport;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
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
    private EditText mOrganization;

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
        mOrganization = (EditText)findViewById(R.id.organization);

        //Buttons
        findViewById(R.id.sign_up_button).setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);


        mUsername.addTextChangedListener(new TextViewValidator(mUsername){ });
        mUsername.setOnFocusChangeListener(new TextViewValidator(mUsername){ });
        mFirstName.addTextChangedListener(new TextViewValidator(mFirstName){ });
        mFirstName.setOnFocusChangeListener(new TextViewValidator(mFirstName){ });
        mLastName.addTextChangedListener(new TextViewValidator(mLastName){ });
        mLastName.setOnFocusChangeListener(new TextViewValidator(mLastName){ });
        mEmail.addTextChangedListener(new TextViewValidator(mEmail){ });
        mEmail.setOnFocusChangeListener(new TextViewValidator(mEmail){ });
        mPassword.addTextChangedListener(new TextViewValidator(mPassword){ });
        mPassword.setOnFocusChangeListener(new TextViewValidator(mPassword){ });
        mConfirmPassword.addTextChangedListener(new TextViewValidator(mConfirmPassword){ });
        mConfirmPassword.setOnFocusChangeListener(new TextViewValidator(mConfirmPassword){ });
        mPhoneNumber.addTextChangedListener(new TextViewValidator(mPhoneNumber){ });
        mPhoneNumber.setOnFocusChangeListener(new TextViewValidator(mPhoneNumber){ });
        mOrganization.addTextChangedListener(new TextViewValidator(mOrganization));
        mOrganization.setOnFocusChangeListener(new TextViewValidator(mOrganization));
        //Get database instance
        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void createUser() {
        Log.d(TAG, "createAccount:");

//        if (validateForm()) {
            //Retrieve form data
            String username = mUsername.getText().toString();
            String firstname = mFirstName.getText().toString();
            String lastname = mLastName.getText().toString();
            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            String phoneNumber = mPhoneNumber.getText().toString();
            String organization = mOrganization.getText().toString();

            //Create a user Object
            AppUser user = new AppUser(username, firstname, lastname, email, phoneNumber, password, organization);

            //Get reference to the users in the database
            mRef = mFirebaseDatabase.getReference().child("users");
            mRef.push().setValue(user);

            // Read from the database
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                //This method is called when the user account is created successfully
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Toast.makeText(RegisterActivity.this, "Account created successfully",
                            Toast.LENGTH_SHORT).show();
                    //Redirect user to sign in
                    signin();
                }

                @Override
                //This method is called when there was an error inserting user data into database
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(RegisterActivity.this, "Failed to create Account",
                            Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        //}
    }

    /**
     * Use firebase UI to sign user into the Application
     */
    public void signin() {
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
     *
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
                Toast.makeText(RegisterActivity.this, "Sign in successful",
                        Toast.LENGTH_SHORT).show();
                Intent postIntent = new Intent(this, PostActivity.class);
                startActivity(postIntent);
            } else {
                // Sign in failed
                if (response == null) {
                    Toast.makeText(RegisterActivity.this, "Signin Cancelled",
                            Toast.LENGTH_SHORT).show();
                    //return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(RegisterActivity.this, "Network Error",
                            Toast.LENGTH_SHORT).show();
                    //return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(RegisterActivity.this, "Unknown error Occurred",
                            Toast.LENGTH_SHORT).show();
                    //return;
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
            createUser();
        } else {

        }
    }

    /**
     * Validate form data
     *
     * @return
     */
    private boolean validateForm() {
        boolean valid = true;

        //Validate email
        String email = mEmail.getText().toString();
        if (!TextUtils.isEmpty(mEmail.getError())) {
            mEmail.setError("Required.");
            valid = false;
        }else {
            mEmail.setError(null);
        }

        //Validate Username
        //String username = mUsername.getText().toString();

        if (!TextUtils.isEmpty(mUsername.getError())) {
            mUsername.setError("Error");
            valid = false;
        } else {
            mUsername.setError(null);
        }

        //Firstname
        //String firstname = mFirstName.getText().toString();
        if (!TextUtils.isEmpty(mFirstName.getError())) {
            mFirstName.setError("Error");
            valid = false;
        } else {
            mFirstName = null;
        }

        //Lastname
        //String lastname = mLastName.getText().toString();
        if (!TextUtils.isEmpty(mLastName.getError())) {
            mLastName.setError("Error");
            valid = false;
        } else {
            mLastName = null;
        }

        //Phone number
        //String phone_number = mPhoneNumber.getText().toString();
        if (!TextUtils.isEmpty(mPhoneNumber.getError())) {
            mPhoneNumber.setError("Error");
            valid = false;
        }else {
            mPhoneNumber = null;
        }
        return valid;
    }
}
