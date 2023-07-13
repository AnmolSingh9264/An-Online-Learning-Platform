package com.anmol.essence.Fragments;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anmol.essence.Activity.Videoplayer;
import com.anmol.essence.Extractor.VideoMeta;
import com.anmol.essence.Extractor.YouTubeExtractor;
import com.anmol.essence.Extractor.YtFile;
import com.anmol.essence.R;
import com.anmol.essence.databinding.ActivityVideoplayerBinding;
import com.anmol.essence.databinding.FragmentVideoBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class video extends Fragment {
    FragmentVideoBinding binding;
    ExoPlayer exoPlayer;
    Boolean flag=false;
    String low,high=null;
    String url;
    BottomSheetDialog dialog;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public video() {
    }
    public static video newInstance(String param1, String param2) {
        video fragment = new video();
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
        binding=FragmentVideoBinding.inflate(getLayoutInflater(),container,false);
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_IMMERSIVE);

        url=getArguments().getString("url");
        dialog= new BottomSheetDialog(requireContext());
        dialog.setContentView(R.layout.videoqualitydialog);
        dialog.setCancelable(true);

        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        binding.player.setPlayer(exoPlayer);
        requireActivity().getWindow().setStatusBarColor(Color.parseColor("#FF000000"));
        Extract(url);
        ProgressBar progressBar = binding.player.findViewById(R.id.progress_bar);
        DefaultTimeBar timeBar = binding.player.findViewById(R.id.exo_progress);
        TextView position = binding.player.findViewById(R.id.exo_position);
        ImageView btn_setting = binding.player.findViewById(R.id.btn_setting);
        TextView duration = binding.player.findViewById(R.id.exo_duration);
        ImageView pause=binding.player.findViewById(R.id.exo_pause);
        ImageView play=binding.player.findViewById(R.id.exo_play);
        position.setVisibility(View.GONE);
        duration.setVisibility(View.GONE);
        timeBar.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onEvents(@NonNull Player player, @NonNull Player.Events events) {

            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_IDLE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.GONE);
                    play.setVisibility(View.GONE);

                }
                if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                    timeBar.setVisibility(View.VISIBLE);
                    exoPlayer.play();
                    duration.setVisibility(View.VISIBLE);
                    position.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.VISIBLE);
                    play.setVisibility(View.VISIBLE);
                }
                if (playbackState == Player.STATE_ENDED) {
                    exoPlayer.release();
                    FragmentManager manager=requireActivity().getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.homelayout,new Details())
                            .commit();
                }
            }
            @Override
            public void onPlayerError(PlaybackException error) {
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlayerErrorChanged(@Nullable PlaybackException error) {

            }

        });
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Extract(url, 18);
                dialog.show();
            }
        });
        RadioGroup group=dialog.findViewById(R.id.radio_group);
        assert group != null;
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton=dialog.findViewById(group.getCheckedRadioButtonId());
                if (radioButton.getText().equals("High Quality")&&group.getCheckedRadioButtonId()!=R.id.btn1){
                    prepareVideo(high,exoPlayer.getCurrentPosition());
                    dialog.dismiss();
                }else if (radioButton.getText().equals("Low Quality")&&group.getCheckedRadioButtonId()!=R.id.btn2){
                    prepareVideo(low,exoPlayer.getCurrentPosition());
                    dialog.dismiss();
                }

            }
        });
        ImageView btn_maximize =binding.player.findViewById(R.id.btn_fullscreen);
        btn_maximize.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if (flag){
                    btn_maximize.setImageDrawable(getResources().getDrawable(R.drawable.fullscreen));
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    binding.player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                    flag=false;
                }else{
                    btn_maximize.setImageDrawable(getResources().getDrawable(R.drawable.minimize));
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    binding.player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    flag=true;
                }
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exoPlayer.release();
                FragmentManager manager=requireActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.homelayout,new Details())
                        .commit();
            }
        });
        return binding.getRoot();
    }
    public void prepareVideo(String url,long time){
        if (url!=null) {
            MediaItem mediaItem = MediaItem.fromUri(url);
            exoPlayer.setMediaItem(mediaItem);
            if (time!=0){
                exoPlayer.seekTo(time);
            }
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.prepare();
        }
    }
    @SuppressLint("StaticFieldLeak")
    public void Extract(String url){
        new YouTubeExtractor(requireContext()) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                high = ytFiles.get(22).getUrl();
                low=ytFiles.get(18).getUrl();
                prepareVideo(high,0);
            }
        }.extract(url, true, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.getPlaybackState();
    }
    @Override
    public void onStop() {
        super.onStop();
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
    }
}