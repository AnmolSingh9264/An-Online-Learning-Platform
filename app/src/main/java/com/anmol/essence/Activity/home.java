package com.anmol.essence.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.anmol.essence.Fragments.Doubts;
import com.anmol.essence.Fragments.Live;
import com.anmol.essence.Fragments.home_fragment;
import com.anmol.essence.Fragments.profile;
import com.anmol.essence.R;
import com.anmol.essence.database.datastore;
import com.anmol.essence.databinding.ActivityHomeBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationBarView;
import com.onesignal.OneSignal;
import com.onesignal.OneSignalDb;

public class home extends AppCompatActivity {
   ActivityHomeBinding binding;
   FragmentManager manager;
   Boolean flag=false;
    private static final String ONESIGNAL_APP_ID = "0aa8cd92-7443-46ca-81a9-4f5bce9142ba";
   int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        manager=getSupportFragmentManager();
        replace(new home_fragment());
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
         OneSignal.promptForPushNotifications();
        binding.navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:replace(new home_fragment());break;
                    case R.id.live:replace(new Live());break;
                    case R.id.doubts:replace(new Doubts());break;
                    case R.id.profile:replace(new profile());break;
                }
                return true;
            }
        });
        /*getWindow().setStatusBarColor(Color.parseColor("#1a237e"));
        sharedPreferences= getSharedPreferences("essance", Context.MODE_PRIVATE);
        datastore=new datastore(sharedPreferences);
        classdialog=new Dialog(home.this);
        classdialog.setContentView(R.layout.classdialog);
        classdialog.setCancelable(false);
        classdialog.create();
        RadioGroup group1=classdialog.findViewById(R.id.radioGroup);
        RadioGroup group2=classdialog.findViewById(R.id.radioGroup2);
        if (TextUtils.isEmpty(datastore.retrive("class"))){
            classdialog.show();
        }else{
            database.child("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    .setValue(datastore.retrive("class"));
        }
        group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton btn=findViewById(group1.getCheckedRadioButtonId());
                datastore.store("class",btn.getText().toString());
                classdialog.dismiss();
            }
        });
        group2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton btn=findViewById(group2.getCheckedRadioButtonId());
                datastore.store("class",btn.getText().toString());
                classdialog.dismiss();
            }
        });*/
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_IMMERSIVE);
       /* location="user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid()).concat("/"+datastore.retrive("class"));
        FirebaseRecyclerOptions<popular_course_model> options=new FirebaseRecyclerOptions.Builder<popular_course_model>()
                 .setQuery(FirebaseDatabase.getInstance().getReference(location+"/popular"),popular_course_model.class).build();
        popular_adapter=new Popular_adapter(options);
        binding.firstlayout.setItemAnimator(null);
        binding.firstlayout.setLayoutManager(new LinearLayoutManager(home.this,LinearLayoutManager.HORIZONTAL,false));
        binding.firstlayout.setNestedScrollingEnabled(true);
        binding.firstlayout.setHasFixedSize(true);
        binding.firstlayout.setAdapter(popular_adapter);




       /* database=FirebaseDatabase.getInstance().getReference("popular_course");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    binding.title.setText(snapshot.child("popular_course").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
      */
    }
    public void replace(Fragment fragment){
        manager.beginTransaction().replace(R.id.homelayout,fragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                i=0;
            }
        },1500);
        if (i==0){
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            i++;
        }else{
            System.exit(0);
        //    finish();
            //super.onBackPressed();
        }*/
    }
}