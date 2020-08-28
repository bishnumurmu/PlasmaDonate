package com.project.plasmadonate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.project.plasmadonate.profile.DonorProfileActivity;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText contactTxt, codeTxt;
    Button getVerifyBtn, signInBtn;

    FirebaseAuth mAuth;

    String codeSent;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactTxt = findViewById(R.id.phone_no);
        getVerifyBtn = findViewById(R.id.get_verification_btn);
        codeTxt = findViewById(R.id.verification_code);
        signInBtn = findViewById(R.id.verify_btn);

        mAuth = FirebaseAuth.getInstance();


        getVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendVerificationCode();

                //String contact = contactTxt.getEditableText().toString();

                //Intent intent = new Intent(getApplicationContext(), VerifyPhoneForLogin.class);
                //intent.putExtra("contact", contact);
                //startActivity(intent);

            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verifySignInCode();

            }
        });

    }

    private void verifySignInCode() {

        String code = codeTxt.getText().toString();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);

        signInWithPhoneAuthCredential(credential);


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(getApplicationContext(), DonorProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "Incorrect Code", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void sendVerificationCode() {

        String contact = contactTxt.getText().toString();

        if(contact.isEmpty()){
            contactTxt.setError("Phone number Required");
            contactTxt.requestFocus();
            return;
        }

        if(contact.length() != 10){
            contactTxt.setError("Enter 10 digit number");
            contactTxt.requestFocus();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+contact,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,            // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        ///Shared shared = new Shared(getApplicationContext());
        ///
        //shared.secondTime();

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            startActivity(new Intent(MainActivity.this, DonorProfileActivity.class));
            finish();
        }
        //else{
            //startActivity(new Intent(MainActivity.this, LoginActivity.class));
        //}

    }
}