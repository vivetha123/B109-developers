package com.example.drugaddictscouncelling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private Button ResetpasswordSendEmailButton;
    private EditText ResetEmailInput;

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();


        setContentView(R.layout.activity_reset_password);
        ResetEmailInput=findViewById(R.id.reset_password_Email);
        ResetpasswordSendEmailButton=findViewById(R.id.reset_password_Email_button);

        mToolbar=findViewById(R.id.forget_password_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");

        ResetpasswordSendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail=ResetEmailInput.getText().toString();
                if (TextUtils.isEmpty(userEmail))
                {
                    ResetEmailInput.setError("required");
                    return;
                }
                else
                {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(ResetPasswordActivity.this,"Please Check Your Email",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(ResetPasswordActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(ResetPasswordActivity.this,"failed",Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            }
        });


    }
}
