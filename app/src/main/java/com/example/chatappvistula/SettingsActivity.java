package com.example.chatappvistula;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button UpdateSettings;
    private EditText userName,userStatus;
    private CircleImageView usernameProfilePic;

    //firebase
    private FirebaseAuth mAuthorize;
    private String validUserId;
    private DatabaseReference dataPath;
    private StorageReference userPicPath;
    private StorageTask loadingTask;

    //Select Pic
    private static final int selectGallery = 1;

    private ProgressDialog loadingBaar;
    //uri
    Uri picUri;
    String myUri = "";

    //toolbar
    private Toolbar settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //FireBase
        mAuthorize= FirebaseAuth.getInstance();
        dataPath= FirebaseDatabase.getInstance().getReference();
        userPicPath= FirebaseStorage.getInstance().getReference().child("ProfilePics");


        validUserId = mAuthorize.getCurrentUser().getUid();


        UpdateSettings = findViewById(R.id.update_setting_button);
        userName = findViewById(R.id.set_username);
        userStatus = findViewById(R.id.profile_status);
        usernameProfilePic = findViewById(R.id.profile_picture);

        //toolbar
        settingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        loadingBaar= new ProgressDialog(this);

        UpdateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateNewSettings();
            }
        });

        userName.setVisibility(View.INVISIBLE);

            BringUserData();

            usernameProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Crop
                    CropImage.activity()
                            .setAspectRatio(1,1)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(SettingsActivity.this);

                }
            });

    }

    private String takeExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    //select pic
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)

        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            picUri = result.getUri();
            usernameProfilePic.setImageURI(picUri);
        }
        
        else
            
        {
            Toast.makeText(this, "Picture did Not Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void BringUserData() {

            dataPath.child("Users").child(validUserId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if((dataSnapshot.exists())&& (dataSnapshot.hasChild("name")&& (dataSnapshot.hasChild("Picture"))))
                            {
                                String bringUsername  = dataSnapshot.child("name").getValue().toString();
                                String bringUserStatu  = dataSnapshot.child("status").getValue().toString();
                                String bringUserPic  = dataSnapshot.child("Picture").getValue().toString();

                                userName.setText(bringUsername);
                                userStatus.setText(bringUserStatu);
                                Picasso.get().load(bringUserPic).into(usernameProfilePic);
                            }

                           else if((dataSnapshot.exists())&& (dataSnapshot.hasChild("name")))
                            {
                                String bringUsername  = dataSnapshot.child("name").getValue().toString();
                                String bringUserStatu  = dataSnapshot.child("status").getValue().toString();


                                userName.setText(bringUsername);
                                userStatus.setText(bringUserStatu);
                            }
                           
                           else 
                            {
                                userName.setVisibility(View.VISIBLE);
                                Toast.makeText(SettingsActivity.this, "Please Fill Your Profile Settings ", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


    }

    private void UpdateNewSettings() {

        String setUsername = userName.getText().toString();
        String setUserStatus = userStatus.getText().toString();

        if (TextUtils.isEmpty(setUsername))

        {
            Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_LONG).show();
        }

        if (TextUtils.isEmpty(setUserStatus))

        {
            Toast.makeText(this, "Please Enter Your Status", Toast.LENGTH_LONG).show();
        }

        else
        {
            uploadPictire();
        }
    }

    private void uploadPictire() {

        loadingBaar.setTitle("Info Transferring");
        loadingBaar.setMessage("Please Wait");
        loadingBaar.setCanceledOnTouchOutside(false);
        loadingBaar.show();

        if(picUri == null)
        {
            DatabaseReference dataPath = FirebaseDatabase.getInstance().getReference().child("Users");

            String sendId = dataPath.push().getKey();

            String bringUsername  = userName.getText().toString();
            String bringUserStatu  = userStatus.getText().toString();

            HashMap<String,Object > profileMap = new HashMap<>();
            profileMap.put("uid",sendId);
            profileMap.put("name",bringUsername);
            profileMap.put("status",bringUserStatu);

            dataPath.child(validUserId).updateChildren(profileMap);

            loadingBaar.dismiss();

        }

        else
        {

            final StorageReference picPath = userPicPath.child(validUserId+"."+takeExtension(picUri));
            loadingTask = picPath.putFile(picUri);

            loadingTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful())

                    {
                        throw task.getException();
                    }

                    return picPath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    //completed task

                    if(task.isSuccessful())
                    {

                        Uri downloadUri=task.getResult();
                        myUri = downloadUri.toString();

                        DatabaseReference dataPath = FirebaseDatabase.getInstance().getReference().child("Users");

                        String sendId = dataPath.push().getKey();

                        String bringUsername  = userName.getText().toString();
                        String bringUserStatu  = userStatus.getText().toString();

                        HashMap<String,Object > profileMap = new HashMap<>();
                        profileMap.put("uid",sendId);
                        profileMap.put("name",bringUsername);
                        profileMap.put("status",bringUserStatu);
                        profileMap.put("Picture",myUri);

                        dataPath.child(validUserId).updateChildren(profileMap);
                        loadingBaar.dismiss();

                    }
                    else
                    {
                        String error = task.getException().toString();
                        Toast.makeText(SettingsActivity.this, "Error:  " +error, Toast.LENGTH_SHORT).show();
                        loadingBaar.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(SettingsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingBaar.dismiss();

                }
            });

        }




    }
}
