package com.anmol.essence.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anmol.essence.R;
import com.anmol.essence.database.datastore;
import com.anmol.essence.databinding.FragmentDetailsBinding;
import com.anmol.essence.model.video_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.anmol.essence.Adapter.video_adapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Details extends Fragment {
    FragmentDetailsBinding binding;
    video_adapter adapter;
    datastore db;
    SharedPreferences preferences;
    DatabaseReference reference;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Details() {
    }
    public static Details newInstance(String param1, String param2) {
        Details fragment = new Details();
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
        binding=FragmentDetailsBinding.inflate(getLayoutInflater(),container,false);
        preferences=requireActivity().getSharedPreferences("essance", Context.MODE_PRIVATE);
        db=new datastore(preferences);
        String url=db.retrive("class").concat("/Chapter/"+db.retrive("title"));
        requireActivity().getWindow().setStatusBarColor(Color.parseColor("#bbdefb"));
        FirebaseRecyclerOptions<video_model> options=new FirebaseRecyclerOptions.Builder<video_model>()
                .setQuery(FirebaseDatabase.getInstance().getReference(url.concat("/Chapters")),video_model.class).build();
        adapter=new video_adapter(options);
        binding.video.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.video.setItemAnimator(null);
        binding.video.setAdapter(adapter);
        BottomNavigationView navigationView=requireActivity().findViewById(R.id.navigation);
        navigationView.setVisibility(View.GONE);
        reference=FirebaseDatabase.getInstance().getReference(url.concat("/Heading"));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.desText.setText(snapshot.child("discription").getValue().toString());
                binding.price.setText(snapshot.child("type").getValue().toString());
                Picasso.get().load(snapshot.child("image").getValue().toString()).into(binding.header);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager=requireActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.homelayout,new home_fragment())
                        .commit();
            }
        });
        binding.title.setText(db.retrive("title"));
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