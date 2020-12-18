package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    private CircleImageView settingsDisplayProfileImage;
    private TextView settingsDisplayName;
    private TextView settingsDisplayStatus;
    private Button settingsChangeProfileImageButton;
    private Button settingsChangeStatusButton;
    String current_user_Id;

    private final static int Gallery_Pick = 1;
    private StorageReference storeProfileImagestoreRef;

    private DatabaseReference getUserDataReference;
    private FirebaseAuth auth;


        public void init()
        {
            auth = FirebaseAuth.getInstance();
            current_user_Id = auth.getCurrentUser().getUid();

            getUserDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_Id);
            storeProfileImagestoreRef = FirebaseStorage.getInstance().getReference().child("Profile_Images");

            settingsDisplayProfileImage = findViewById(R.id.settings_profile_image);
            settingsDisplayName = findViewById(R.id.txtMyUserName);
            settingsDisplayStatus = findViewById(R.id.myStatus);
            settingsChangeProfileImageButton = findViewById(R.id.change_picture_button);
            settingsChangeStatusButton = findViewById(R.id.change_status_button);

        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();

        //veri okuma
        getUserDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();


                settingsDisplayName.setText(name);
                settingsDisplayStatus.setText(status);
                if (!image.equals("default_profile")) {
                    Picasso.get().load(image).into(settingsDisplayProfileImage);


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /* Galeriden profil resmi seçmeme yarayacak. */
        settingsChangeProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);

            }
        });
        settingsChangeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                startActivity(statusIntent);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
            if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data!=null) {
                super.onActivityResult(requestCode, resultCode, data);
                Uri ImageUri = data.getData();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);

            }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri();
                String user_id = auth.getCurrentUser().getUid();
                StorageReference filePath=storeProfileImagestoreRef.child(user_id + ".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {

                                final String downloadUrl = uri.toString();
                                getUserDataReference.child("user_image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {

                                            Picasso.get().load(downloadUrl).into(settingsDisplayProfileImage);
                                            Toast.makeText(SettingsActivity.this,"Resim başarıyla güncellendi!",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                            }
                        });
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }
}