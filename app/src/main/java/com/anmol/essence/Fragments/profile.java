package com.anmol.essence.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.anmol.essence.Activity.accounts;
import com.anmol.essence.R;
import com.anmol.essence.database.datastore;
import com.anmol.essence.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import java.util.Objects;

public class profile extends Fragment {
    FragmentProfileBinding binding;
    datastore datastore;
    SharedPreferences preferences;
    FirebaseAuth auth;
    DatabaseReference dbref,share;
    FirebaseStorage storage;
    StorageReference reference;
    Dialog name,classdialog;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public profile() {
    }
    public static profile newInstance(String param1, String param2) {
        profile fragment = new profile();
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
        auth=FirebaseAuth.getInstance();
       requireActivity().getWindow().setStatusBarColor(Color.parseColor("#FF000000"));
       binding=FragmentProfileBinding.inflate(getLayoutInflater(),container,false);
       preferences=requireActivity().getSharedPreferences("essance", Context.MODE_PRIVATE);
       datastore=new datastore(preferences);
       binding.name.setText(datastore.retrive("name"));
       storage=FirebaseStorage.getInstance();
       name=new Dialog(requireContext());
       name.setContentView(R.layout.update_dialog);
       name.setCancelable(true);
       name.create();
       dbref= FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
       reference=storage.getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        ActivityResultLauncher<String> launcher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result!=null){
                   reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                  dbref.child("profile").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void unused) {
                                          snack("Profile updated successfully").show();
                                          new Handler().postDelayed(new Runnable() {
                                              @Override
                                              public void run() {
                                                  snack("Profile updated successfully").dismiss();
                                              }
                                          },2000);
                                      }
                                  });
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           });
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                          snack(e.getMessage()).show();
                          new Handler().postDelayed(new Runnable() {
                              @Override
                              public void run() {
                                  snack(e.getMessage()).dismiss();
                              }
                          },1500);
                       }
                   });
                }
            }
        });
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("profile").exists()){
                    Picasso.get().load(snapshot.child("profile").getValue().toString())
                            .into(binding.profileImage);
                    datastore.store("profile",snapshot.child("profile").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.profileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent=new Intent(requireContext(), accounts.class);
                startActivity(intent);
                requireActivity().finish();
                datastore.clear();
            }
        });
        EditText editText=name.findViewById(R.id.name);
        TextInputLayout layout=name.findViewById(R.id.textInputLayout);
        MaterialButton update=name.findViewById(R.id.update);
        binding.changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.show();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editText.getText())){
                    layout.setError("");
                }else{
                    datastore.store("name",editText.getText().toString());
                    name.dismiss();
                    Snackbar.make(binding.getRoot(),"Name updated Successfully",BaseTransientBottomBar.LENGTH_LONG)
                            .show();
                }
            }
        });
        binding.changeClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classdialog.show();
            }
        });
        classdialog=new Dialog(requireContext());
        classdialog.setContentView(R.layout.classdialog);
        classdialog.setCancelable(true);
        classdialog.create();
        RadioGroup group1=classdialog.findViewById(R.id.radioGroup);
        RadioGroup group2=classdialog.findViewById(R.id.radioGroup2);
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
        binding.shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share=FirebaseDatabase.getInstance().getReference("share");
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                share.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sendIntent.putExtra(Intent.EXTRA_TEXT,snapshot.getValue().toString());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
        return binding.getRoot();
    }
    public Snackbar snack(String msg){
        return Snackbar.make(binding.getRoot(),msg, BaseTransientBottomBar.LENGTH_LONG);
    }
}