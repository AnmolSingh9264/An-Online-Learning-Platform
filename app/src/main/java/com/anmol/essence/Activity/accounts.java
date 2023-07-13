package com.anmol.essence.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;

import com.anmol.essence.Fragments.Signup;
import com.anmol.essence.R;

public class accounts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        getSupportActionBar().hide();
        FragmentManager manager=getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.accounts_layout,new Signup()).commit();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}