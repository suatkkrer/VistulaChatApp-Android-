package com.example.chatappvistula;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText usermail,userpasswod;
    private TextView haveAlreadyAccount;

    //FireBase
    private DatabaseReference rootReference;
    private FirebaseAuth mAuthorize;

    private ProgressDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Firebase

        mAuthorize=FirebaseAuth.getInstance();
        rootReference= FirebaseDatabase.getInstance().getReference();


        //Controls
        registerButton = findViewById(R.id.register_button);

        usermail= findViewById(R.id.register_mail);
        userpasswod= findViewById(R.id.register_password);

        haveAlreadyAccount=findViewById(R.id.have_account);

        loadingDialog = new ProgressDialog(this);

        haveAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent loginActivityIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(loginActivityIntent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateNewAccount();
            }

            private void CreateNewAccount()
            {
                String email = usermail.getText().toString();
                String password = userpasswod.getText().toString();

                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivity.this, "Email can not be empty", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password))
                {
                    Toast.makeText(RegisterActivity.this, "Password can not be empty", Toast.LENGTH_SHORT).show();
                }

                else
                    {

                        loadingDialog.setTitle("New Account Is Creating");
                        loadingDialog.setMessage("Please Wait...");
                        loadingDialog.setCanceledOnTouchOutside(true);
                        loadingDialog.show();


                    mAuthorize.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful())

                                    {
                                        String validUserId=mAuthorize.getCurrentUser().getUid();
                                        rootReference.child("Users").child(validUserId).setValue("");






                                        Intent mainPage = new Intent(RegisterActivity.this,MainActivity.class);
                                        mainPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                        startActivity(mainPage);
                                        finish();

                                        Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                    }
                                    else
                                    {
                                        String message = task.getException().toString();
                                        Toast.makeText(RegisterActivity.this, "Error: "+ message+"Check Your Information ", Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                    }


                                }
                            });
                    }


                }
        });

    }
}
