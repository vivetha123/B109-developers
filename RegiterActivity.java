package com.example.drugaddictscouncelling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegiterActivity extends AppCompatActivity {


    private EditText UserEmail,UserPassword,UserConfirmPassword;
    private Button CreateAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiter);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        UserEmail=findViewById(R.id.register_email);
        UserPassword=findViewById(R.id.register_password);
        UserConfirmPassword=findViewById(R.id.register_confirm_password);
        CreateAccountButton=findViewById(R.id.register_create_account);
        textView=findViewById(R.id.loginTxtView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegiterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });



    }
    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser!=null)
        {
            SendUserToMainActivity();
        }

    }

    private void SendUserToMainActivity()
    {
        Intent intent=new Intent(RegiterActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void CreateNewAccount()
    {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmpassword = UserConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(email))
        {
            UserEmail.setError("Email is Required");
            return;
        }
        if (TextUtils.isEmpty(password))
        {
            UserPassword.setError("Password is Required");
            return;
        }
        if (password.length() < 6) {
            UserPassword.setError("password must be greater then or equal to six");
            return;
        }
        if (TextUtils.isEmpty(confirmpassword))
        {
            UserEmail.setError("Confirm Password is Required");
            return;
        }
        else if (!password.equals(confirmpassword))
        {
            Toast.makeText(RegiterActivity.this, "Your Passsord does not match", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait,while we are creating your new account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        SendUserToMainUpActivity();
                        Toast.makeText(RegiterActivity.this, "You Are Authenticated Suiceessfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(RegiterActivity.this, "Error Occured" + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
            });

        }

    }

    private void SendUserToMainUpActivity()
    {
        Intent intent=new Intent(RegiterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}


