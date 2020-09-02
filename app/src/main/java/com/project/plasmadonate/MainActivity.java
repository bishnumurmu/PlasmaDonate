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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.plasmadonate.authentication.RegisterActivity;
import com.project.plasmadonate.messages.ChatsActivity;
import com.project.plasmadonate.profile.DashboardActivity;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText contactTxt, codeTxt;
    Button getVerifyBtn, signInBtn, registerHereBtn;

    FirebaseAuth mAuth;

    String codeSent = "4959697989";

    FirebaseUser user;

    String nameFromDB, addressFromDB, emailFromDB, cityFromDB;

    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactTxt = findViewById(R.id.phone_no);
        getVerifyBtn = findViewById(R.id.get_verification_btn);
        codeTxt = findViewById(R.id.verification_code);
        signInBtn = findViewById(R.id.verify_btn);
        //
        registerHereBtn = findViewById(R.id.register_here_btn);

        mAuth = FirebaseAuth.getInstance();

        getVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getUserData();
                //see if user exists or not
                if(flag != 2){
                    sendVerificationCode();
                } else{
                    flag = 0;
                    contactTxt.setError("You are not a registered user");
                }

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

        registerHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                takeMeToRegistrationPage();

            }
        });



    }

    private void takeMeToRegistrationPage() {

        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);

    }

    private void verifySignInCode() {

        String code = codeTxt.getText().toString();

        if(code.isEmpty()){
            codeTxt.setError("OTP Required");
            codeTxt.requestFocus();
            return;
        }

        if(code.length() != 6){
            codeTxt.setError("Enter 6 digit One time Password");
            codeTxt.requestFocus();
            return;
        }

        if(codeSent.equals("4959697989")){
            codeTxt.setError("Invalid code");
        }

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
                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //
                            intent.putExtra("name",nameFromDB);
                            intent.putExtra("city",cityFromDB);
                            intent.putExtra("address",addressFromDB);
                            intent.putExtra("email",emailFromDB);
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
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }
        //else{
            //startActivity(new Intent(MainActivity.this, LoginActivity.class));
        //}

    }

    private void getUserData(){

        final String contact = contactTxt.getEditableText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("donors").orderByChild("contact").equalTo(contact);

        Query query1 = reference.child("patients").orderByChild("contact").equalTo(contact);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    nameFromDB = snapshot.child(contact).child("name").getValue(String.class);
                    emailFromDB = snapshot.child(contact).child("email").getValue(String.class);
                    cityFromDB = snapshot.child(contact).child("city").getValue(String.class);
                    addressFromDB = snapshot.child(contact).child("address").getValue(String.class);
                }
                else{
                    flag++;
                    //contactTxt.setError("Phone number not registered");
                    //contactTxt.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    nameFromDB = snapshot.child(contact).child("name").getValue(String.class);
                    emailFromDB = snapshot.child(contact).child("email").getValue(String.class);
                    cityFromDB = snapshot.child(contact).child("city").getValue(String.class);
                    addressFromDB = snapshot.child(contact).child("address").getValue(String.class);
                }
                else{
                    flag++;
                    //contactTxt.setError("Phone number not registered");
                    //contactTxt.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}