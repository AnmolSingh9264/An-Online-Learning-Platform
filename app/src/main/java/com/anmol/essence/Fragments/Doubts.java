package com.anmol.essence.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anmol.essence.Adapter.doubt_adapter;
import com.anmol.essence.R;
import com.anmol.essence.database.datastore;
import com.anmol.essence.databinding.FragmentDoubtsBinding;
import com.anmol.essence.model.doubt_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class Doubts extends Fragment {
    FragmentDoubtsBinding binding;
    Boolean flag=false,already=false;
    DatabaseReference reference;
    SharedPreferences preferences;
    datastore db;
    String doubt;
    doubt_adapter adapter;
    AlertDialog.Builder  dialog;
    StorageReference storageReference;
    Boolean imgFlag=false;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Doubts() {
        // Required empty public constructor
    }
    public static Doubts newInstance(String param1, String param2) {
        Doubts fragment = new Doubts();
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
        binding=FragmentDoubtsBinding.inflate(getLayoutInflater(),container,false);
        requireActivity().getWindow().setStatusBarColor(Color.rgb(255,255,255));
        dialog=new AlertDialog.Builder(requireContext());
        storageReference= FirebaseStorage.getInstance().getReference("doubt/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        preferences=requireActivity().getSharedPreferences("essance", Context.MODE_PRIVATE);
        db=new datastore(preferences);
        reference= FirebaseDatabase.getInstance().getReference(db.retrive("class").concat("/Doubt/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid()));
        dialog.setCancelable(true);
        dialog.setTitle("Warning");
        dialog.setIcon(R.drawable.warning2);
        dialog.create();
        dialog.setMessage("Your previous doubt will be deleted");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                already=false;
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        //dialog.show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("qus").exists()){
                    binding.qustionContainar.setVisibility(View.VISIBLE);
                    Picasso.get().load(snapshot.child("qus").getValue().toString())
                            .into(binding.doubtImg);
                    if (snapshot.child("sub").exists() && snapshot.child("des").exists()){
                        binding.textView6.setText(snapshot.child("sub").getValue().toString());
                        binding.textView8.setText(snapshot.child("des").getValue().toString());
                        already=true;
                    }
                    if (snapshot.child("ans").exists()) {
                        if (!TextUtils.isEmpty(snapshot.child("ans").getValue().toString())) {
                            Picasso.get().load(snapshot.child("ans").getValue().toString()).into(binding.img);
                            binding.img.setVisibility(View.VISIBLE);
                        } else {
                            binding.img.setVisibility(View.GONE);
                        }
                    }
                }else{
                    binding.qustionContainar.setVisibility(View.GONE);
                    already=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (already){
                    dialog.show();
                } else if (TextUtils.isEmpty(binding.name.getText())){
                    binding.textInputLayout.setError("Subject Name");
                }else if (TextUtils.isEmpty(binding.description.getText())){
                    binding.textInputLayou2.setError("Problem Statement");
                }else if (!flag){
                    imgFlag=true;
                    Toast.makeText(requireContext(), "Kindly upload question", Toast.LENGTH_SHORT).show();
                }
                else{
                    reference.child("sub").setValue(binding.name.getText().toString());
                    reference.child("des").setValue(binding.description.getText().toString());
                    reference.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference.child("ans").setValue("");
                    imgFlag=false;
                }
            }
        });
        ActivityResultLauncher<String> launcher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result!=null){
                    storageReference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    reference.child("qus").setValue(uri.toString());
                                    reference.child("sub").setValue(binding.name.getText().toString());
                                    reference.child("des").setValue(binding.description.getText().toString());
                                    reference.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    reference.child("ans").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(requireActivity(), "Doubt  uploaded successfully", Toast.LENGTH_SHORT).show();
                                            imgFlag=false;
                                            binding.name.setText("");
                                            binding.description.setText("");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            imgFlag=true;
                                        }
                                    });
                                    binding.upload.setEnabled(true);
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
                            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else
                    flag=false;
            }
        });
        binding.doubtImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(already){
                    dialog.show();
                }else if (TextUtils.isEmpty(binding.name.getText())){
                    binding.textInputLayout.setError("Subject Name");
                }else if (TextUtils.isEmpty(binding.description.getText())){
                    binding.textInputLayou2.setError("Problem Statement");
                } else{
                    launcher.launch("image/*");
                }
            }
        });
       /* FirebaseRecyclerOptions<doubt_model> options=new FirebaseRecyclerOptions.Builder<doubt_model>()
                .setQuery(FirebaseDatabase.getInstance().getReference(db.retrive("class").concat("/Doubt/"+FirebaseAuth.getInstance().getCurrentUser().getUid())), doubt_model.class)
                .build();
        adapter=new doubt_adapter(options);
        binding.answer.setItemAnimator(null);
        binding.answer.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.answer.setHasFixedSize(true);
        binding.answer.setAdapter(adapter);*/
        return binding.getRoot();
    }

    public void setDoubt(String doubt) {
        this.doubt = doubt;
    }
    public String getDoubt(){
        return doubt;
    }

    @Override
    public void onStop() {
        super.onStop();
      //  adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
       // adapter.startListening();
    }
}