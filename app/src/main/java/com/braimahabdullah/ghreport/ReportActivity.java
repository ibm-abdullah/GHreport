package com.braimahabdullah.ghreport;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Report issues Activity";
    private static final int TAKE_PICTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 300;

    private ImageView imageView;
    private TextView textView;
    private TextView mTitle;
    private Button mSendBtn;
    private Button mTakePictureBtn;
    private Button mGetLocation;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase mFirebaseDatabase;
    private Uri file;
    private String downlaodUri;
    private String imageFileName;
    private String imagePath;

    private LocationManager locationManager;
    Location location;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mTitle = findViewById(R.id.title);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.issue);
        mSendBtn = findViewById(R.id.send);
        mTakePictureBtn = findViewById(R.id.picture);
        mGetLocation = findViewById(R.id.location);
        mSendBtn.setEnabled(false);

        textView.addTextChangedListener(new TextViewValidator(textView));
        textView.setOnFocusChangeListener(new TextViewValidator(textView));
        mTitle.addTextChangedListener(new TextViewValidator(mTitle));
        mTitle.setOnFocusChangeListener(new TextViewValidator(mTitle));

        mSendBtn.setOnClickListener(this);
        mTakePictureBtn.setOnClickListener(this);
        mGetLocation.setOnClickListener(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        //Check for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            mTakePictureBtn.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mTakePictureBtn.setEnabled(true);
            }
        } else if (requestCode == PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Use implicit intent to take picture
     */
    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                file = FileProvider.getUriForFile(ReportActivity.this, BuildConfig.APPLICATION_ID +
                        ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                startActivityForResult(intent, TAKE_PICTURE);
            } else {
                Toast.makeText(this, "Error Creating File", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {

        }
    }

    /**
     * A callback to handle picture snapping status
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(null);
                imageView.setImageURI(file);
                Toast.makeText(this, "Add location", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error Taking Picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Post send picture to the cload database
     */
    public void sendPicture() {

        //Process image into bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference imagesRef = storageRef.child("images").child(imageFileName);

        //Get image properties
        //imagePath = imagesRef.getPath();

        //Upload image to firebase storage
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ReportActivity.this, "File could not be upload. Try again",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ReportActivity.this, "Image successfully uploaded",
                        Toast.LENGTH_SHORT).show();
                downlaodUri = taskSnapshot.getDownloadUrl().toString();

                addPost();
            }
        });
    }

    public void addPost() {
        Post newPost = new Post(mTitle.getText().toString(), imageFileName, imagePath, downlaodUri,
                textView.getText().toString());
        DatabaseReference mRef = mFirebaseDatabase.getReference().child("posts");
        mRef.push().setValue(newPost);

        // Read from the database
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Toast.makeText(ReportActivity.this, "Issue posted successfully",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Issues  posted");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(ReportActivity.this, "Failed to Post issue",
                        Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void addLocation() {
        GPSTracker gps = new GPSTracker(ReportActivity.this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            mSendBtn.setEnabled(true);
            Toast.makeText(this, "Current" + latitude + "" + longitude, Toast.LENGTH_SHORT).show();
        } else {
            gps.showSettingsAlert();
            Toast.makeText(this, "Click button to add location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.send) {
            if (imageView == null) {
                Toast.makeText(ReportActivity.this, "Add a picture to your or video to issue",
                        Toast.LENGTH_SHORT).show();
            } else {
                sendPicture();
            }
        } else if (i == R.id.picture) {
            takePhoto();
        } else if (i == R.id.location) {
            addLocation();
        } else {

        }
    }

    /**
     * Used to create a Image file
     *
     * @return Image file
     * @throws IOException exception
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //imagePath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out_menu) {
            //Toast.makeText(this,"Signing out",Toast.LENGTH_SHORT).show();
            signUserOut();
        }
        return true;
    }

    /**
     * Sign user out of the application
     */
    public void signUserOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(ReportActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
