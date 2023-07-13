package com.anmol.essence.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anmol.essence.R;
import com.anmol.essence.databinding.FragmentLivePlayScreenBinding;
import com.anmol.essence.youtube_player;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class live_play_screen extends Fragment {
    FragmentLivePlayScreenBinding binding;
    BottomNavigationView navigationView;
    youtube_player player;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public live_play_screen() {
        // Required empty public constructor
    }

    public static live_play_screen newInstance(String param1, String param2) {
        live_play_screen fragment = new live_play_screen();
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
        binding=FragmentLivePlayScreenBinding.inflate(inflater,container,false);
        navigationView=requireActivity().findViewById(R.id.navigation);
        navigationView.setVisibility(View.GONE);
      //  player =new youtube_player(requireContext());
        getLifecycle().addObserver(binding.youtubePlayerView);
        binding.youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                youTubePlayer.loadVideo(getArguments().getString("video"),0);
            }
        });
        requireActivity().getWindow().setStatusBarColor(Color.rgb(0,0,0));
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager manager=requireActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.homelayout,new Live()).commit();
            }
        });
        return binding.getRoot();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.youtubePlayerView.release();
    }
}