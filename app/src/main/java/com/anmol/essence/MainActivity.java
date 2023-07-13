package com.anmol.essence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.anmol.essence.Activity.accounts;
import com.anmol.essence.Activity.home;
import com.anmol.essence.database.datastore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
     FirebaseAuth auth;
     datastore datastore;
     SharedPreferences sharedPreferences;
     DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        sharedPreferences=getSharedPreferences("essance", Context.MODE_PRIVATE);
        datastore=new datastore(sharedPreferences);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_IMMERSIVE);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(datastore.retrive("class")) && auth.getCurrentUser()!=null){
                    Intent intent = new Intent(MainActivity.this, accounts.class);
                    startActivity(intent);
                    finish();
                }else if(auth.getCurrentUser()!=null ) {
                    Intent intent = new Intent(MainActivity.this, home.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(MainActivity.this, accounts.class);
                    startActivity(intent);
                    finish();
                }
            }
        },5000);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(Color.rgb(255,255,255));


        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            database= FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("class").exists()){
                        datastore.store("class",snapshot.child("class").getValue().toString());

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}