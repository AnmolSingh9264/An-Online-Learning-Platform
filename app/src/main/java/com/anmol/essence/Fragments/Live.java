package com.anmol.essence.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anmol.essence.Adapter.Live_adapter;
import com.anmol.essence.R;
import com.anmol.essence.database.datastore;
import com.anmol.essence.databinding.FragmentLiveBinding;
import com.anmol.essence.model.live_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

public class Live extends Fragment {
    FragmentLiveBinding binding;
    Live_adapter adapter;
    datastore db;
    SharedPreferences preferences;
    BottomNavigationView navigationView;
    int trigger=0;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Live() {
        // Required empty public constructor
    }
    public static Live newInstance(String param1, String param2) {
        Live fragment = new Live();
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
        binding=FragmentLiveBinding.inflate(getLayoutInflater(),container,false);
        navigationView=requireActivity().findViewById(R.id.navigation);
        if (navigationView.getVisibility()==View.GONE){
            navigationView.setVisibility(View.VISIBLE);
        }
        requireActivity().getWindow().setStatusBarColor(Color.rgb(255,255,255));
        preferences= requireActivity().getSharedPreferences("essance", Context.MODE_PRIVATE);
        db=new datastore(preferences);
        FirebaseRecyclerOptions<live_model> options=new FirebaseRecyclerOptions.Builder<live_model>()
                .setQuery(FirebaseDatabase.getInstance().getReference(db.retrive("class").concat("/live")), live_model.class)
                .build();
        adapter=new Live_adapter(options);
        binding.liveclasses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.liveclasses.setItemAnimator(null);
        binding.liveclasses.setAdapter(adapter);
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
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}