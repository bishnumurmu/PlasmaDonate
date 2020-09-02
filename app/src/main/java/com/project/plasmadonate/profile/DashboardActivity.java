package com.project.plasmadonate.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.plasmadonate.MainActivity;
import com.project.plasmadonate.R;
import com.project.plasmadonate.patients.SeePatientsActivity;

import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    Button logOutBtn;
    Button goBtn;

    FirebaseUser user;

    String user_name, user_city, user_address, user_email;
    String nameFromDB, addressFromDB, emailFromDB, cityFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        logOutBtn = findViewById(R.id.logout);
        goBtn = findViewById(R.id.go_btn);

        user = FirebaseAuth.getInstance().getCurrentUser();

        showAllUserData();
        getUserData();

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                finish();
            }
        });

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, SeePatientsActivity.class);
                //
                intent.putExtra("name", nameFromDB);
                intent.putExtra("city", cityFromDB);
                intent.putExtra("address", addressFromDB);
                intent.putExtra("email", emailFromDB);
                //
                startActivity(intent);
            }
        });
    }

    private void getUserData(){

        //
        //get phone number from getPhoneNumber() and remove the country code
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String getting_phone_no = Objects.requireNonNull(user.getPhoneNumber()).trim();
        final String getting_phone_substring = getting_phone_no.substring(getting_phone_no.length() - 10);
        //

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("donors").orderByChild("contact").equalTo(getting_phone_substring);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    nameFromDB = snapshot.child(getting_phone_substring).child("name").getValue(String.class);
                    emailFromDB = snapshot.child(getting_phone_substring).child("email").getValue(String.class);
                    cityFromDB = snapshot.child(getting_phone_substring).child("city").getValue(String.class);
                    addressFromDB = snapshot.child(getting_phone_substring).child("address").getValue(String.class);
                }
                else{
                    //contactTxt.setError("Phone number not registered");
                    //contactTxt.requestFocus();
                    Toast.makeText(DashboardActivity.this, "The data does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showAllUserData() {

        Intent intent = getIntent();
        user_name = intent.getStringExtra("name");
        user_city = intent.getStringExtra("city");
        user_address = intent.getStringExtra("address");
        user_email = intent.getStringExtra("email");

    }

}