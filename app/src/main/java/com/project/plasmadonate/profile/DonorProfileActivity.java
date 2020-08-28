package com.project.plasmadonate.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.plasmadonate.MainActivity;
import com.project.plasmadonate.R;

public class DonorProfileActivity extends AppCompatActivity {

    Button logOutBtn;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);

        logOutBtn = findViewById(R.id.logout);

        user = FirebaseAuth.getInstance().getCurrentUser();

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DonorProfileActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}