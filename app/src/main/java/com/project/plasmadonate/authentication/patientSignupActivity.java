package com.project.plasmadonate.authentication;

import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;

        import com.project.plasmadonate.R;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;

public class patientSignupActivity extends AppCompatActivity {

    EditText nameTxt, addressTxt, passwordTxt, emailTxt, contactTxt;

    Button regBtn;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_signup);

        nameTxt = findViewById(R.id.name);
        addressTxt = findViewById(R.id.address);
        passwordTxt = findViewById(R.id.password);
        emailTxt = findViewById(R.id.email);
        contactTxt = findViewById(R.id.contact);

        regBtn = findViewById(R.id.register1);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("patients");

                if(!validateName() | !validateAddress() | !validateEmail() | !validatePassword() | !validateContact()){
                    return;
                }

                //get all the values
                String name = nameTxt.getEditableText().toString();
                String address = addressTxt.getEditableText().toString();
                String email = emailTxt.getEditableText().toString();
                String password = passwordTxt.getEditableText().toString();
                String contact = contactTxt.getEditableText().toString();


                patientHelperActivity patientHelperActivity = new patientHelperActivity(name, address, email, password, contact);
                reference.child(contact).setValue(patientHelperActivity);

                startActivity(new Intent(patientSignupActivity.this, patientThanksActivity.class));
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
        } else {
            contactTxt.setError(null);
            return true;
        }
    }
}