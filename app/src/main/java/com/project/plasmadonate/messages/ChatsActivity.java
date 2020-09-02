package com.project.plasmadonate.messages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.plasmadonate.R;
import com.project.plasmadonate.profile.DashboardActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText editText;
    ImageButton sendBtn;
    RecyclerView recyclerView;
    TextView userName, userStatus;
    ImageView profilePhoto;

    String hisUid, myUid;
    //String hisImage

    FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDBRef;
    //for checking if user has seen a message
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        //find view by ID
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        recyclerView = findViewById(R.id.recycler_view);
        editText = findViewById(R.id.edit_message);
        sendBtn = findViewById(R.id.image_button);
        profilePhoto = findViewById(R.id.image_profile);

        userName = findViewById(R.id.name);
        userStatus = findViewById(R.id.status);

        ///
        //Layout of RecyclerView

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatsActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        //recyclerView properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ///

        ///
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("contact");

        firebaseDatabase = FirebaseDatabase.getInstance();
        userDBRef = firebaseDatabase.getReference("patients");

        /// my get me as a user, get my phone number
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String gettingPhoneNo = user.getPhoneNumber();
        myUid = gettingPhoneNo.substring(gettingPhoneNo.length() - 10);

        //search user
        Query query = userDBRef.orderByChild("contact").equalTo(hisUid);
        /// get user picture and name
        // currently doing just name, will do the picture later
        ///////
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){

                    //get data
                    String name = ""+ds.child("name").getValue();

                    //set data
                    userName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get text from edit text
                String message = editText.getText().toString().trim();
                // check if message is empty
                if(TextUtils.isEmpty(message)){
                    //Empty
                    Toast.makeText(ChatsActivity.this, "Empty message", Toast.LENGTH_SHORT).show();

                }else{
                    //Not empty
                    sendMessage(message);

                }
            }
        });

        readMessages();

        seenMessage();


    }

    private void seenMessage() {

        userRefForSeen = FirebaseDatabase.getInstance().getReference("chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen", true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readMessages() {

        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                     chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }
                    //adapter
                    adapterChat = new AdapterChat(ChatsActivity.this, chatList);
                    adapterChat.notifyDataSetChanged();
                    //set adapter to recyclerview
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //new
    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("isSeen", false);
        databaseReference.child("chats").push().setValue(hashMap);

        // Reset the edit text after sending message
        editText.setText("");


    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            ///stay here
        } else{
            startActivity(new Intent(ChatsActivity.this, DashboardActivity.class));
            finish();
        }
    }
    //new
    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }
}