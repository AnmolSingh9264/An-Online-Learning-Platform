package com.anmol.essence.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anmol.essence.Adapter.Batch_adapter;
import com.anmol.essence.Adapter.Games_adapter;
import com.anmol.essence.Adapter.Popular_adapter;
import com.anmol.essence.R;
import com.anmol.essence.database.datastore;
import com.anmol.essence.databinding.FragmentViewAllBinding;
import com.anmol.essence.model.batch_model;
import com.anmol.essence.model.games_model;
import com.anmol.essence.model.popular_course_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

public class View_all extends Fragment {
    FragmentViewAllBinding binding;
    Popular_adapter adapter=null;
    Batch_adapter batch_adapter=null;
    Games_adapter games_adapter=null;
    datastore datastore;
    SharedPreferences preferences;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public View_all() {
    }

    public static View_all newInstance(String param1, String param2) {
        View_all fragment = new View_all();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentViewAllBinding.inflate(getLayoutInflater(),container,false);
        preferences= requireActivity().getSharedPreferences("essance", Context.MODE_PRIVATE);
        datastore=new datastore(preferences);
        requireActivity().getWindow().setStatusBarColor(Color.rgb(255,255,255));
        BottomNavigationView navigationView=requireActivity().findViewById(R.id.navigation);
        if (navigationView.getVisibility()==View.VISIBLE){
            navigationView.setVisibility(View.GONE);
        }
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_IMMERSIVE);
       if (TextUtils.equals(getArguments().getString("location"),datastore.retrive("class").concat("/popular"))){
           FirebaseRecyclerOptions<popular_course_model> options=new FirebaseRecyclerOptions.Builder<popular_course_model>()
                   .setQuery(FirebaseDatabase.getInstance().getReference(getArguments().getString("location")),popular_course_model.class).build();
           adapter=new Popular_adapter(options);
       }else if (getArguments().getString("location").equals(datastore.retrive("class").concat("/special"))){
           FirebaseRecyclerOptions<batch_model> options2=new FirebaseRecyclerOptions.Builder<batch_model>()
                   .setQuery(FirebaseDatabase.getInstance().getReference(getArguments().getString("location")), batch_model.class).build();
           batch_adapter=new Batch_adapter(options2);
       }else{
           FirebaseRecyclerOptions<games_model> options3=new FirebaseRecyclerOptions.Builder<games_model>()
                   .setQuery(FirebaseDatabase.getInstance().getReference(getArguments().getString("location")), games_model.class).build();
           games_adapter=new Games_adapter(options3);
       }
        binding.firstlayout.setItemAnimator(null);
        binding.firstlayout.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (adapter!=null){
            binding.firstlayout.setAdapter(adapter);
        }else if (batch_adapter!=null){
            binding.firstlayout.setAdapter(batch_adapter);
        }else if (games_adapter!=null){
            binding.firstlayout.setAdapter(games_adapter);
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager manager=requireActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.homelayout,new home_fragment()).commit();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter!=null){
            adapter.startListening();
        }else if (batch_adapter!=null){
            batch_adapter.startListening();
        }else if (games_adapter!=null){
            games_adapter.startListening();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter!=null){
            adapter.startListening();
        }else if (batch_adapter!=null){
            batch_adapter.startListening();
        }else if (games_adapter!=null){
            games_adapter.startListening();
        }
    }
}