package com.anmol.essence.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anmol.essence.Adapter.video_adapter;
import com.anmol.essence.R;
import com.anmol.essence.database.datastore;
import com.anmol.essence.databinding.FragmentBatchVideosBinding;
import com.anmol.essence.model.video_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Batch_videos extends Fragment {
    video_adapter adapter;
    datastore db;
    SharedPreferences preferences;
    DatabaseReference reference;
    FragmentBatchVideosBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Batch_videos() {
    }
    public static Batch_videos newInstance(String param1, String param2) {
        Batch_videos fragment = new Batch_videos();
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
        binding=FragmentBatchVideosBinding.inflate(getLayoutInflater(),container,false);
        requireActivity().getWindow().setStatusBarColor(Color.rgb(255,255,255));
        preferences=requireActivity().getSharedPreferences("essance", Context.MODE_PRIVATE);
        db=new datastore(preferences);
        String url=getArguments().getString("path");
        FirebaseRecyclerOptions<video_model> options=new FirebaseRecyclerOptions.Builder<video_model>()
                .setQuery(FirebaseDatabase.getInstance().getReference(db.retrive("class").concat("/"+url)+"/Chapters"),video_model.class).build();
        adapter=new video_adapter(options);
        binding.video.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.video.setItemAnimator(null);
        binding.video.setAdapter(adapter);
        BottomNavigationView navigationView=requireActivity().findViewById(R.id.navigation);
        navigationView.setVisibility(View.GONE);
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager manager=requireActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.homelayout,new home_fragment())
                        .commit();
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
        adapter.stopListening();
    }
}