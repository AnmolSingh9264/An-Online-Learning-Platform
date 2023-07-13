package com.anmol.essence.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class datastore {
    SharedPreferences sharedPreferences;
    public datastore(SharedPreferences sharedPreferences){
        this.sharedPreferences=sharedPreferences;
    }
    public void store(String key,String data){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key,data);
        editor.apply();
    }
    public String retrive(String key){
       return sharedPreferences.getString(key,"");
    }
    public void clear(){
         SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
