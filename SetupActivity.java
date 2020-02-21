package com.example.drugaddictscouncelling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity
{

    private EditText UserName,FullName,CountryName;
    private Button SaveInformationButton;
    private CircleImageView profileImage;
    private FirebaseAuth  mAuth;
    private DatabaseReference UsersRef;
    String currentUserID;
    private ProgressDialog loadingBar;
    final static int Gallery_Pick=1;
    private StorageReference UserProfileImageRef;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mAuth=FirebaseAuth.getInstance();
        UserProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

        currentUserID=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        loadingBar=new ProgressDialog(this);
        UserName=findViewById(R.id.setup_username);
        FullName=findViewById(R.id.setup_fullname);
        CountryName=findViewById(R.id.setup_country_name);

        SaveInformationButton=findViewById(R.id.setup_information_button);
        profileImage=findViewById(R.id.setup_profile_image);

        SaveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pick);
            }
        });
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {

                    if (dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(profileImage);

                    }
                    else

                    {
                        Toast.makeText(SetupActivity.this,"Please Select Profile Image",Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallery_Pick && resultCode==RESULT_OK && data != null)
        {
            Uri ImageUri=data.getData();
            CropImage.activity(ImageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK)
            {
                loadingBar.setTitle(" Profile Image");
                loadingBar.setMessage("Please wait,while we updating your profile image");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Uri resultUri=result.getUri();
                final StorageReference filePath=UserProfileImageRef.child(currentUserID+".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl=uri.toString();
                                UsersRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(SetupActivity.this, SetupActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(SetupActivity.this, "Profile Image Stored Successfully", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }

                                        else
                                        {
                                            String meessage=task.getException().getMessage();
                                            Toast.makeText(SetupActivity.this,"Error Occured"+meessage,Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }

                                    }
                                });
                            }
                        });
                    }
                });

            }
            else
            {
                Toast.makeText(SetupActivity.this,"Error occured:Image Can't be cropped try again",Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }

    }

    private void SaveAccountSetupInformation()
    {
        String username=UserName.getText().toString();
        String fullname=FullName.getText().toString();
        String country=CountryName.getText().toString();


        if (TextUtils.isEmpty(country))
        {
            CountryName.setError("CountryName is Required");
            return;

        }
        if (TextUtils.isEmpty(username))
        {
            CountryName.setError("username is Required");
            return;

        }
        if (TextUtils.isEmpty(fullname))
        {
            CountryName.setError("fullname is Required");
            return;

        }


        else

        {

            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait,while we are Creating Your New account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);


            HashMap usersMap=new HashMap();
            usersMap.put("username",username);
            usersMap.put("fullname",fullname);
            usersMap.put("country",country);
            usersMap.put("status","Hey There i am using social Netork");
            usersMap.put("gender","none");
            usersMap.put("dob","none");
            usersMap.put("relationshipstatus","none");


            UsersRef.updateChildren(usersMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {


                    if (task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this,"Your Account Is Created",Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();

                    }
                    else
                    {
                        String message=task.getException().getMessage();
                        Toast.makeText(SetupActivity.this,"Error Occured"+message,Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
            });
        }

    }


    private void SendUserToMainActivity()
    {

        Intent intent=new Intent(SetupActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}



