package com.braimahabdullah.ghreport;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Report issues Activity";
    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;
    private ImageView imageView;
    private TextView textView;
    private Button mSendBtn;
    private Button mTakePictureBtn;
    private Button mTakeVideoBtn;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.issue);
        mSendBtn = findViewById(R.id.send);
        mTakePictureBtn = findViewById(R.id.picture);
        mTakeVideoBtn = findViewById(R.id.video);

        mSendBtn.setOnClickListener(this);
        mTakePictureBtn.setOnClickListener(this);
        mTakeVideoBtn.setOnClickListener(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String filename = generateImageFileName();
        File photo = new File(Environment.getExternalStorageDirectory(), filename + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == ReportActivity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);

                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    try {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        imageView.setImageBitmap(bitmap);
                        // Get the data from an ImageView as bytes
                        imageView.setDrawingCacheEnabled(true);
                        imageView.buildDrawingCache();
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
        }
    }

    public void sendPicture() {

        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = firebaseStorage.getReference();
        //StorageReference imagesRef = storageRef.child("images");
        //Get filename
        String filename = generateImageFileName();

        //reference to the child image file
        StorageReference spaceRef = storageRef.child("images/" + filename + ".jpg");

        // File path is "images/imageName.jpg"//
        String path = spaceRef.getPath();
        String name = spaceRef.getName();
        String issue = textView.getText().toString();
        post = new Post(path, name, issue);

        UploadTask uploadTask = spaceRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                post.setDownlableUri(downloadUrl);

                DatabaseReference mRef = firebaseDatabase.getReference().child("posts");
                mRef.push().setValue(post);

                // Read from the database
                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = dataSnapshot.getValue(String.class);
                        Toast.makeText(ReportActivity.this, "Issue posted",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Value is: " + value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Toast.makeText(ReportActivity.this, "Failled to Post issue",
                                Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
            }
        });
        Toast.makeText(this, filename.toString(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.send) {
            if(imageView == null){
                Toast.makeText(ReportActivity.this, "Failled to Post issue",
                        Toast.LENGTH_SHORT).show();
            }else{
                sendPicture();
            }
        } else if (i == R.id.picture) {
            takePhoto();
        } else if (i == R.id.video) {

        } else {

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public String generateImageFileName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String imageID = dateFormat.format(date).toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();

            imageID = imageID + uid;
        }
        return imageID;
    }
}
