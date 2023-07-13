package com.anmol.essence.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.anmol.essence.Activity.home;
import com.anmol.essence.R;
import com.anmol.essence.databinding.FragmentSignupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.anmol.essence.database.datastore;

public class Signup extends Fragment {
    FirebaseAuth auth;
    FragmentSignupBinding binding;
    DatabaseReference reference,database;
    SharedPreferences sharedPreferences;
    datastore datastore;
    Dialog classdialog;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Signup() {
    }

    public static Signup newInstance(String param1, String param2) {
        Signup fragment = new Signup();
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
        requireActivity().getWindow().setStatusBarColor(Color.parseColor("#212121"));
        auth=FirebaseAuth.getInstance();
        sharedPreferences= requireActivity().getSharedPreferences("essance", Context.MODE_PRIVATE);
        datastore=new datastore(sharedPreferences);
        binding=FragmentSignupBinding.inflate(getLayoutInflater(),container,false);
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager=requireActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.accounts_layout,new Login())
                        .commit();
            }
        });
        binding.appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                     register();
                }
            }
        });
        classdialog=new Dialog(requireContext());
        classdialog.setContentView(R.layout.classdialog);
        classdialog.setCancelable(false);
        classdialog.create();
        if (auth.getCurrentUser()!=null) {
            database=FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
            add_class();
        }
        return binding.getRoot();
    }
    public Boolean validate(){
        if(TextUtils.isEmpty(binding.editText4.getText())){
            show_snack("Phone number is empty","#c62828");
            return false;
        }else if (binding.editText4.getText().toString().length()<10){
            show_snack("Phone number should be 10 digits","#c62828");
            return false;
        }
        else if (TextUtils.isEmpty(binding.editText.getText())){
            show_snack("Email is empty","#c62828");
            return false;
        }else if(TextUtils.isEmpty(binding.editText2.getText())){
            show_snack("Name is empty","#c62828");
            return false;
        }
        else if(TextUtils.isEmpty(binding.editText3.getText())){
            show_snack("Password is empty","#c62828");
            return false;
        }else{
            return true;
        }
    }
    public void show_snack(String msg,String color){
        Snackbar.make(binding.getRoot(),msg, BaseTransientBottomBar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor(color)).show();
    }
    public void register(){
        binding.progressbar.setVisibility(View.VISIBLE);
        binding.appCompatButton.setEnabled(false);
        binding.login.setEnabled(false);
        auth.createUserWithEmailAndPassword(binding.editText.getText().toString(),binding.editText3.getText().toString()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                thread(e.getMessage(),"#c62828",false);
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                thread("Successfully registered","#66bb6a",true);
                reference= FirebaseDatabase.getInstance().getReference("user");
            }
        });
    }
    public void thread(String msg,String color,Boolean acknowlegement){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.login.setEnabled(true);
                binding.appCompatButton.setEnabled(true);
                show_snack(msg,color);
                binding.progressbar.setVisibility(View.GONE);
                if(acknowlegement){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            datastore.store("name",binding.editText2.getText().toString());
                            datastore.store("email",binding.editText.getText().toString());
                            datastore.store("password",binding.editText3.getText().toString());
                            datastore.store("phone",binding.editText4.getText().toString());
                            add_class();
                        }
                    },1000);

                }
            }
        },3000);
    }
    public void add_class(){
        RadioGroup group1=classdialog.findViewById(R.id.radioGroup);
        RadioGroup group2=classdialog.findViewById(R.id.radioGroup2);
        classdialog.show();
        group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton btn = classdialog.findViewById(group1.getCheckedRadioButtonId());
                datastore.store("class", btn.getText().toString());
                classdialog.dismiss();
                Intent intent=new Intent(requireContext(), home.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        group2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton btn = classdialog.findViewById(group2.getCheckedRadioButtonId());
                datastore.store("class", btn.getText().toString());
                classdialog.dismiss();
                Intent intent=new Intent(requireContext(), home.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }
}