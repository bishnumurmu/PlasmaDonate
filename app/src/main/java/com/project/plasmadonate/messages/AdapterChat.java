package com.project.plasmadonate.messages;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.plasmadonate.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

//error coming see the error

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    //String imageUrl;
    //I will use the image later

    FirebaseUser firebaseUser;


    public AdapterChat(Context context, List<ModelChat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.user_chat_right, parent, false);
            return new MyHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.user_chat_left, parent, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {

        String messageDB = chatList.get(position).getMessage();
        String timeStampDB = chatList.get(position).getTimestamp();

        //Calender
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStampDB));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set data
        holder.messageTv.setText(messageDB);
        holder.timeTv.setText(dateTime);

        //image will be done later

        //click to show the delete dialog
        holder.messageLAyout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show delete message
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Sure? Want to delete the message");
                //delete button
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        deleteMessage(position);

                    }
                });
                //cancel delete button
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                //create and show dialog
                builder.create().show();

            }
        });

        //set seen/delivered
        if(position == chatList.size()-1){
            if(chatList.get(position).isSeen()){
                holder.isSeenTv.setText("Seen");
            }
            else{
                holder.isSeenTv.setText("Delivered");
            }
        }
        else {
            holder.isSeenTv.setVisibility(View.GONE);
        }

    }

    private void deleteMessage(int position) {
        String phoneNo = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        final String getting_phone_substring = phoneNo.substring(phoneNo.length() - 10);
        //process
        //get timestamp of the clicked message
        //compare the timestamp of the clicked message with all messages
        //if both values matches, the delete message
        String msgTimeStamp = chatList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    /*if we want user to only delete his message then
                    compare sender value to that of the user's uid, if that
                    matches means sender is trying to delete his message
                     */
                    if(ds.child("sender").getValue().equals(getting_phone_substring)){
                        //2 things
                        //i. remove message
                        //or ii. set value as "message deleted"
                        //i.
                        //can use this one too --> ds.getRef().removeValue();
                        //ii.
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "Message was deleted");
                        ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "Only allowed to delete your own message", Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //Get currently signed in user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String phoneNo = firebaseUser.getPhoneNumber();
        final String getting_phone_substring = phoneNo.substring(phoneNo.length() - 10);
        if(chatList.get(position).getSender().equals(getting_phone_substring)){
            return MSG_TYPE_RIGHT;
        } else{
            return MSG_TYPE_LEFT;
        }
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views
        ImageView profileIv;
        TextView messageTv, timeTv, isSeenTv;
        LinearLayout messageLAyout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profileIv = itemView.findViewById(R.id.chat_profile);
            messageTv = itemView.findViewById(R.id.chat_message);
            timeTv = itemView.findViewById(R.id.chat_time);
            isSeenTv = itemView.findViewById(R.id.chat_sent);
            messageLAyout = itemView.findViewById(R.id.messageLayout);
        }
    }

}
