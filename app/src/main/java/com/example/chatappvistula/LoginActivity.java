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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton,loginwithphoneButton;
    private EditText usermail, userpassword;
    private TextView createnewaccount,forgotpassword;

    //Firebase

    private FirebaseAuth mAuthorize;

    //Progress
    ProgressDialog loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Controls
        loginButton=findViewById(R.id.login_button);
        loginwithphoneButton=findViewById(R.id.enter_with_phone);

        usermail = findViewById(R.id.login_mail);
        userpassword = findViewById(R.id.login_password);

        createnewaccount = findViewById(R.id.create_new_account);
        forgotpassword = findViewById(R.id.login_forgot_password);

        //Progress

        loginDialog=new ProgressDialog(this);

        //Firebase

        mAuthorize=FirebaseAuth.getInstance();


        createnewaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerActivityIntent= new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerActivityIntent);
            }
        });

        loginwithphoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent phoneEntry = new Intent(LoginActivity.this,PhoneAccountActivity.class);
                startActivity(phoneEntry);
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AuthorizetoLogin();
            }

            private void AuthorizetoLogin()

            {
                String email= usermail.getText().toString();
                String password= userpassword.getText().toString();

                if(TextUtils.isEmpty(email))
                
                {
                    Toast.makeText(LoginActivity.this, "Email can not be empty", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(password))

                {
                    Toast.makeText(LoginActivity.this, "Password can not be empty", Toast.LENGTH_SHORT).show();

                }
                else
                {

                    //Progress
                    loginDialog.setTitle("Logging Your Account");
                    loginDialog.setMessage("Please Wait");
                    loginDialog.setCanceledOnTouchOutside(true);
                    loginDialog.show();

                    mAuthorize.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) 
                                    {
                                        Intent mainPage = new Intent(LoginActivity.this,MainActivity.class);
                                        mainPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainPage);
                                        finish();
                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        loginDialog.dismiss();

                                    }
                                    else
                                        {
                                        String message = task.getException().toString();
                                            Toast.makeText(LoginActivity.this, "Error: "+message+ " Check Your Information", Toast.LENGTH_SHORT).show();
                                            loginDialog.dismiss();
                                    }

                                }
                            });

                }


            }
        });



    }



    private void SendUserToMainActivity() {

        Intent mainactivityintent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainactivityintent);
    }
}

