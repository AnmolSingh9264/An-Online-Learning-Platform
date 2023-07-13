package com.anmol.essence.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anmol.essence.Activity.home;
import com.anmol.essence.R;
import com.anmol.essence.database.datastore;
import com.anmol.essence.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Login extends Fragment {

    FragmentLoginBinding binding;
    FirebaseAuth auth;
    DatabaseReference database;
    com.anmol.essence.database.datastore datastore;
    SharedPreferences sharedPreferences;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public Login() {
    }
    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
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
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().getWindow().setStatusBarColor(Color.parseColor("#ffb300"));
        binding=FragmentLoginBinding.inflate(getLayoutInflater(),container,false);
        sharedPreferences=requireActivity().getSharedPreferences("essance", Context.MODE_PRIVATE);
        datastore=new datastore(sharedPreferences);
        auth=FirebaseAuth.getInstance();
        binding.forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.editText.getText())){
                    show_snack("Email is empty","#c62828");
                }else{
                    password_reset();
                }
            }
        });
        binding.createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager=requireActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.accounts_layout,new Signup())
                        .commit();
            }
        });
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate(binding.editText.getText().toString(),binding.editText3.getText().toString())){
                    login(binding.editText.getText().toString(),binding.editText3.getText().toString());
                }
            }
        });
        return binding.getRoot();
    }
    public void show_snack(String msg,String color){
        Snackbar.make(binding.getRoot(),msg, BaseTransientBottomBar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor(color)).show();
    }
    public Boolean validate(String email,String password){
        if (TextUtils.isEmpty(email)){
            show_snack("Email is empty","#c62828");
            return false;
        }else if(TextUtils.isEmpty(password)){
            show_snack("Password is empty","#c62828");
            return false;
        }else{
            return true;
        }
    }
    public void password_reset(){
        binding.progressbar.setVisibility(View.VISIBLE);
        binding.login.setEnabled(false);
        binding.createaccount.setEnabled(false);
        binding.forgotpass.setEnabled(false);
        auth.sendPasswordResetEmail(binding.editText.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                thread("Password reset link sent to your email","#66bb6a",false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                thread(e.getMessage(),"#c62828",false);
            }
        });
    }
    public void thread(String msg,String color,Boolean acknowlegement){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                show_snack(msg,color);
                binding.progressbar.setVisibility(View.GONE);
                if(acknowlegement){
                    Intent intent=new Intent(requireContext(), home.class);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                    startActivity(intent);
                    requireActivity().finish();
                        }
                    },1000);
                    binding.login.setEnabled(true);
                    binding.createaccount.setEnabled(true);
                    binding.forgotpass.setEnabled(true);
                }
            }
        },3000);
    }
    public void login(String email,String password){
        binding.progressbar.setVisibility(View.VISIBLE);
        binding.login.setEnabled(false);
        binding.createaccount.setEnabled(false);
        binding.forgotpass.setEnabled(false);
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                database= FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("class").exists()){
                            datastore.store("class",snapshot.child("class").getValue().toString());
                            thread("Successfully Logged in","#66bb6a",true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                thread(e.getMessage(),"#c62828",false);

            }
        });
    }
}