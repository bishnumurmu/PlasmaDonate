package com.project.plasmadonate.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.project.plasmadonate.R;

public class RegisterActivity extends AppCompatActivity {

    Button donorBtn, patientBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        donorBtn = findViewById(R.id.donor);
        patientBtn = findViewById(R.id.patient);

        donorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, donorSignupActivity.class));
            }
        });

        patientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, donorSignupActivity.class));
            }
        });
    }
}