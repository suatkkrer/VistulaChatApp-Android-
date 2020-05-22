package com.example.chatappvistula;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.ProgressBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAccountActivity extends AppCompatActivity {

    private Button SendVerificationCodeButton,VerifyButton;
    private EditText EnterPhoneNumber,EnterVerifictaionCode;

    //PhoneVerification

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    //Firebase

    FirebaseAuth mAuth;

    //Loading

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_account);

        //Descriptions
        SendVerificationCodeButton=findViewById(R.id.send_verification_code);
        VerifyButton=findViewById(R.id.verify_button);

        EnterPhoneNumber=findViewById(R.id.phone_number_input);
        EnterVerifictaionCode=findViewById(R.id.verification_code);

        //progress dialog

        loadingBar = new ProgressDialog(this);

        //firebase Desc

        mAuth=FirebaseAuth.getInstance();

        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String phoneNumber = EnterPhoneNumber.getText().toString();
                
                if (TextUtils.isEmpty(phoneNumber))
                {
                    Toast.makeText(PhoneAccountActivity.this, "Phone Number can not be empty", Toast.LENGTH_LONG).show();
                }

                else
                {

                    //loading

                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please Wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneAccountActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }



            }
        });

        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String verificationCode = EnterVerifictaionCode.getText().toString();

                if(TextUtils.isEmpty(verificationCode))

                {
                    Toast.makeText(PhoneAccountActivity.this, "Verification code can not be empty", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    //loading

                    loadingBar.setTitle("Code Verification");
                    loadingBar.setMessage("Please Wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });



        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)

            {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e)

            {

                loadingBar.dismiss();

                Toast.makeText(PhoneAccountActivity.this, "Invalid Phone Number.Please Enter Your Country Code...", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loadingBar.dismiss();

                Toast.makeText(PhoneAccountActivity.this, "Code is sent", Toast.LENGTH_LONG).show();

                //Visibility settings

                SendVerificationCodeButton.setVisibility(View.VISIBLE);
                VerifyButton.setVisibility(View.VISIBLE);

                EnterPhoneNumber.setVisibility(View.VISIBLE);
                EnterVerifictaionCode.setVisibility(View.VISIBLE);
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneAccountActivity.this, "You entered you account", Toast.LENGTH_LONG).show();
                            sendUserToMainPage();
                        }
                        else {


                            String errorMessage = task.getException().toString();

                            Toast.makeText(PhoneAccountActivity.this, "Error:  "+errorMessage, Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    private void sendUserToMainPage() {
        Intent sendMainPage = new Intent(PhoneAccountActivity.this,MainActivity.class);
        sendMainPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendMainPage);
        finish();

    }

}
