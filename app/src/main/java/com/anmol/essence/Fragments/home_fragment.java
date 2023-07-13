package com.anmol.essence.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.anmol.essence.Activity.home;
import com.anmol.essence.Adapter.Batch_adapter;
import com.anmol.essence.Adapter.Games_adapter;
import com.anmol.essence.Adapter.Popular_adapter;
import com.anmol.essence.R;
import com.anmol.essence.database.datastore;
import com.anmol.essence.databinding.FragmentHomeFragmentBinding;
import com.anmol.essence.model.batch_model;
import com.anmol.essence.model.games_model;
import com.anmol.essence.model.popular_course_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

public class home_fragment extends Fragment {
    FragmentHomeFragmentBinding binding;
    DatabaseReference database,notice,game;
    Popular_adapter popular_adapter;
    Games_adapter game_adapter;
    com.anmol.essence.database.datastore datastore;
    Dialog classdialog;
    SharedPreferences sharedPreferences;
    Batch_adapter batch_adapter;
    Dialog notice_dialog;
    int trigger=0;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public home_fragment() {
        // Required empty public constructor
    }

    public static home_fragment newInstance(String param1, String param2) {
        home_fragment fragment = new home_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentHomeFragmentBinding.inflate(getLayoutInflater(),container,false);
        requireActivity().getWindow().setStatusBarColor(Color.parseColor("#1a237e"));
        sharedPreferences= requireActivity().getSharedPreferences("essance", Context.MODE_PRIVATE);
        datastore=new datastore(sharedPreferences);
        classdialog=new Dialog(requireContext());
        classdialog.setContentView(R.layout.classdialog);
        classdialog.setCancelable(false);
        classdialog.create();
        notice=FirebaseDatabase.getInstance().getReference("notice");
        notice_dialog=new Dialog(requireContext());
        notice_dialog.setCancelable(false);
        notice_dialog.setContentView(R.layout.notice_dialog);
        notice_dialog.create();
        TextView notice_text=notice_dialog.findViewById(R.id.notice);
        BottomNavigationView navigationView=requireActivity().findViewById(R.id.navigation);
        if (navigationView.getVisibility()==View.GONE){
            navigationView.setVisibility(View.VISIBLE);
        }
        database=FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (!TextUtils.isEmpty(datastore.retrive("name"))&& !TextUtils.isEmpty(datastore.retrive("email"))&& !TextUtils.isEmpty(datastore.retrive("phone"))) {
            database.child("name").setValue(datastore.retrive("name"));
            database.child("email").setValue(datastore.retrive("email"));
            database.child("phone").setValue(datastore.retrive("phone"));
            database.child("password").setValue(datastore.retrive("password"));
        }
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("name").exists()){
                    datastore.store("name",snapshot.child("name").getValue().toString());
                }else if (snapshot.child("profile").exists()){
                    datastore.store("profile",snapshot.child("profile").getValue().toString());
                    Picasso.get().load(snapshot.child("profile").getValue().toString()).into(binding.profile);
                }else if(snapshot.child("class").exists()){
                    OneSignal.sendTag("class",snapshot.child("class").getValue().toString());
                    datastore.store("class",snapshot.child("class").getValue().toString());
                }
                if (!snapshot.child("class").exists()){
                   // if (TextUtils.isEmpty(datastore.retrive("class"))){
                        classdialog.show();
                 //   }else{
                     //   database.child("class")
                         //       .setValue(datastore.retrive("class"));
                   // }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        classdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                database.child("class")
                       .setValue(datastore.retrive("class"));
            }
        });
        database.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        RadioGroup group1=classdialog.findViewById(R.id.radioGroup);
        RadioGroup group2=classdialog.findViewById(R.id.radioGroup2);
        if (!TextUtils.isEmpty(datastore.retrive("class"))) {
            database.child("class")
                    .setValue(datastore.retrive("class"));
        }
        group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton btn=classdialog.findViewById(group1.getCheckedRadioButtonId());
                datastore.store("class",btn.getText().toString());
                classdialog.dismiss();
            }
        });
        group2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton btn=classdialog.findViewById(group2.getCheckedRadioButtonId());
                datastore.store("class",btn.getText().toString());
                classdialog.dismiss();
            }
        });
        binding.scrollView.setSmoothScrollingEnabled(true);
        binding.scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if(i1==0){
                    navigationView.setVisibility(View.VISIBLE);
                }else{
                    navigationView.setVisibility(View.GONE);
                }
            }
        });


        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_IMMERSIVE);
        FirebaseRecyclerOptions<popular_course_model> options=new FirebaseRecyclerOptions.Builder<popular_course_model>()
                .setQuery(FirebaseDatabase.getInstance().getReference(datastore.retrive("class").concat("/popular")),popular_course_model.class).build();
        popular_adapter=new Popular_adapter(options);

        binding.firstlayout.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.firstlayout.setItemAnimator(null);
       // binding.firstlayout.setHasFixedSize(true);
        binding.firstlayout.setAdapter(popular_adapter);
        FirebaseRecyclerOptions<batch_model> batch=new FirebaseRecyclerOptions.Builder<batch_model>()
                .setQuery(FirebaseDatabase.getInstance().getReference(datastore.retrive("class").concat("/special")), batch_model.class).build();
        batch_adapter=new Batch_adapter(batch);
        binding.layout2.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.layout2.setItemAnimator(null);
        binding.layout2.setAdapter(batch_adapter);

        game=FirebaseDatabase.getInstance().getReference(datastore.retrive("class").concat("/games"));
        game.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    binding.title3.setVisibility(View.VISIBLE);
                }else{
                    binding.title3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseRecyclerOptions<games_model> game=new FirebaseRecyclerOptions.Builder<games_model>()
                .setQuery(FirebaseDatabase.getInstance().getReference(datastore.retrive("class").concat("/games")),games_model.class).build();
        game_adapter=new Games_adapter(game);
        binding.games.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.games.setItemAnimator(null);
        binding.games.setAdapter(game_adapter);
        notice.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if(notice_dialog.isShowing()) {
                        notice_text.setText(snapshot.getValue().toString());
                    }else{
                        notice_dialog.show();
                        notice_text.setText(snapshot.getValue().toString());
                    }
                }else{
                    if (notice_dialog.isShowing()) {
                        notice_dialog.dismiss();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.all1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    ViewAll("/popular");
            }
        });
        binding.all2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewAll("/special");
            }
        });
        binding.all3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewAll("/games");
            }
        });
        if (!TextUtils.isEmpty(datastore.retrive("profile"))){
            Picasso.get().load(datastore.retrive("profile")).into(binding.profile);
        }

        binding.notification2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog=new Dialog(requireContext());
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.notification_dialog);
                dialog.create();
                dialog.show();
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                trigger++;
                if (trigger==1){
                    Toast.makeText(requireContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
                }else{
                    System.exit(0);
                    requireActivity().finish();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       trigger=0;
                    }
                },2000);
            }
        });
        return binding.getRoot();
    }
    @Override
    public void onStop() {
        super.onStop();
        popular_adapter.stopListening();
        batch_adapter.stopListening();
        game_adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        popular_adapter.startListening();
        batch_adapter.startListening();
            game_adapter.startListening();
    }
    public void ViewAll(String path){
        Bundle bundle=new Bundle();
        bundle.putString("location",datastore.retrive("class").concat(path));
        View_all view_all=new View_all();
        view_all.setArguments(bundle);
        FragmentManager manager=requireActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.homelayout,view_all).addToBackStack(null).commit();
    }
}