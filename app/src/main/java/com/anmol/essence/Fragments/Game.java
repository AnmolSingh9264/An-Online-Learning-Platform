package com.anmol.essence.Fragments;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.anmol.essence.R;
import com.anmol.essence.databinding.FragmentGameBinding;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Game extends Fragment {
    FragmentGameBinding binding;
    Boolean flag=false;
    StringBuilder adservers;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Game() {
    }

    public static Game newInstance(String param1, String param2) {
        Game fragment = new Game();
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

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameBinding.inflate(getLayoutInflater(), container, false);
           // readAdServers();
            binding.webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            binding.webView.setScrollbarFadingEnabled(true);
            binding.webView.setLongClickable(true);
            binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            binding.webView.setWebViewClient(new MyWebViewClient());
            requireActivity().getWindow().setStatusBarColor(Color.rgb(255, 255, 255));
            registerForContextMenu(binding.webView);
            WebSettings webSettings = binding.webView.getSettings();
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAppCacheEnabled(true);
            webSettings.setAppCachePath(requireContext().getCacheDir().getAbsolutePath());
            binding.webView.loadUrl(getArguments().getString("data"));

            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo ani = cm.getActiveNetworkInfo();
            if (ani != null && ani.isConnected())
                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            else
                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

            webSettings.setAllowFileAccess(false);
            webSettings.setJavaScriptEnabled(true);                        // Enable this only if you need JavaScript support!
            webSettings.setJavaScriptCanOpenWindowsAutomatically(false);   // Enable this only if you want pop-ups!
            webSettings.setMediaPlaybackRequiresUserGesture(true);
            binding.rotate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (flag) {
                        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                        flag = false;
                    } else {
                        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        flag = true;
                        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    }
                }
            });
            requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    binding.webView.destroy();
                    FragmentManager manager = requireActivity().getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.homelayout, new home_fragment())
                            .commit();
                }
            });
        return binding.getRoot();
    }
    private void readAdServers() {
        String line = "";
        adservers = new StringBuilder();

        InputStream is = this.getResources().openRawResource(R.raw.adblockserverlist);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        if (is != null) {
            try {
                while ((line = br.readLine()) != null) {
                    adservers.append(line);
                    adservers.append("\n");
                }
            } catch (IOException | OutOfMemoryError e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            ByteArrayInputStream EMPTY = new ByteArrayInputStream("".getBytes());
            String kk5 = String.valueOf(adservers);

            if (kk5.contains(":::::" + request.getUrl().getHost())) {
                return new WebResourceResponse("text/plain", "utf-8", EMPTY);
            }
            return super.shouldInterceptRequest(view, request);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.webView.destroy();
    }
}