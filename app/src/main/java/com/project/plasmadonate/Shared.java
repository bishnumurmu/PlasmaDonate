package com.project.plasmadonate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.project.plasmadonate.profile.DonorProfileActivity;

public class Shared {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //context
    Context context;
    //mode should be private for sharedpref
    int mode=0;
    //sharedpref file name
    String Filename="sdfile";
    //store the boolean value wrt key id
    String Data="b";

    //create constructor to pass memory at runtime
    // alt + insert


    public Shared(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences(Filename, mode);
        editor=sharedPreferences.edit();
    }

    //for second time users
    public void secondTime(){
        editor.putBoolean(Data, true);
        editor.commit();
    }

    //for first time users
    public void firstTime(){
        if(this.login()){

            Intent intent = new Intent(context, DonorProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }
    }

    private boolean login() {
        return sharedPreferences.getBoolean(Data,false);
    }
}
