package com.project.plasmadonate.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.plasmadonate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class donorSignupActivity extends AppCompatActivity {

    EditText nameTxt, addressTxt, cityTxt, passwordTxt, emailTxt, contactTxt;

    Button regBtn;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_signup);

        nameTxt = findViewById(R.id.name);
        addressTxt = findViewById(R.id.address);
        cityTxt = findViewById(R.id.city);
        passwordTxt = findViewById(R.id.password);
        emailTxt = findViewById(R.id.email);
        contactTxt = findViewById(R.id.contact);

        regBtn = findViewById(R.id.register2);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("donors");

                //
                if(!validateName() | !validateAddress() | !validateCity() | !validateEmail() | !validatePassword() | !validateContact()){
                    return;
                }

                //get all the values
                final String name = nameTxt.getEditableText().toString();
                final String address = addressTxt.getEditableText().toString();
                final String city = cityTxt.getEditableText().toString();
                final String email = emailTxt.getEditableText().toString();
                final String password = passwordTxt.getEditableText().toString();
                final String contact = contactTxt.getEditableText().toString();

                //-----------------------------------------------///////
                //reference.orderByChild("contact").equalTo(contact).addListenerForSingleValueEvent(new ValueEventListener() {

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(checkForPhoneNumber(contact, snapshot)){

                            Toast.makeText(donorSignupActivity.this, "Account with this number already exists",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(getApplicationContext(), VerifyPhoneNo.class);
                            intent.putExtra("contact", contact);
                            startActivity(intent);

                            //
                            donorHelperClass donorHelperClass = new donorHelperClass(name, address, city, email, password, contact);
                            reference.child(contact).setValue(donorHelperClass);
                            //
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //----------------------------------------------///////

                //startActivity(new Intent(donorSignupActivity.this, donorThanksActivity.class));
            }
        });

    }

    private boolean validateName() {
        String val = nameTxt.getEditableText().toString();
        if (val.isEmpty()) {
            nameTxt.setError("Name cannot be empty");
            return false;
        } else {
            nameTxt.setError(null);
            return true;
        }
    }

    private boolean validateAddress() {
        String val = addressTxt.getEditableText().toString();
        if (val.isEmpty()) {
            addressTxt.setError("Address cannot be empty");
            return false;
        } else {
            addressTxt.setError(null);
            return true;
        }
    }

    private boolean validateCity() {
        String val = cityTxt.getEditableText().toString();
        if (val.isEmpty()) {
            cityTxt.setError("City name cannot be empty");
            return false;
        } else {
            emailTxt.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String val = emailTxt.getEditableText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            emailTxt.setError("Email cannot be empty");
            return false;
        } else if(!val.matches(emailPattern)){
            emailTxt.setError("Invalid Email");
            return false;
        } else {
            emailTxt.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = passwordTxt.getEditableText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            passwordTxt.setError("Password cannot be empty");
            return false;
        }
        else if(!val.matches(passwordVal)){
            passwordTxt.setError("Weak Password");
            return false;
        }
        else {
            passwordTxt.setError(null);
            return true;
        }
    }

    private boolean validateContact() {
        String val = contactTxt.getEditableText().toString();
        if (val.isEmpty()) {
            contactTxt.setError("Contact No. cannot be empty");
            return false;
        } else if (val.length() != 10){
            contactTxt.setError("Enter a 10 digit number");
            return false;
        }else {
            contactTxt.setError(null);
            return true;
        }
    }

    boolean checkForPhoneNumber(String contact, DataSnapshot dataSnapshot){

        donorHelperClass donorHelperClass = new donorHelperClass();

        for(DataSnapshot ds: dataSnapshot.getChildren()){
            donorHelperClass.setContact(ds.getValue(com.project.plasmadonate.authentication.donorHelperClass.class).getContact());

            if((donorHelperClass.getContact()).equals(contact)){
                Toast.makeText(donorSignupActivity.this, "Already exists", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

}