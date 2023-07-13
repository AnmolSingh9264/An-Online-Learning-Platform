package com.anmol.essence.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anmol.essence.Fragments.Batch_videos;
import com.anmol.essence.R;
import com.anmol.essence.database.datastore;
import com.anmol.essence.model.batch_model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class Batch_adapter extends FirebaseRecyclerAdapter<batch_model,Batch_adapter.Viewholder> {
    public Batch_adapter(@NonNull FirebaseRecyclerOptions<batch_model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Batch_adapter.Viewholder holder, int position, @NonNull batch_model model) {
        String  path=holder.db.retrive("class").concat("/special/").concat(model.getPath());
        holder.title.setText(model.getTitle());
        holder.duration.setText(model.getDuration());
        holder.time.setText(model.getTime());
        Picasso.get().load(model.getImg()).into(holder.img);
        holder.reference= FirebaseDatabase.getInstance().getReference(path);
        holder.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Integer.parseInt(model.getOccupied())>=Integer.parseInt(snapshot.child("size").getValue().toString())){
                    holder.join.setText("Batch full");
                    holder.join.setEnabled(false);
                }
                else{
                    holder.join.setText("Join");
                    holder.join.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.joined=FirebaseDatabase.getInstance().getReference(holder.db.retrive("class").concat("/"+model.getPath()));
        holder.joined.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.reference=FirebaseDatabase.getInstance().getReference(path.concat("/occupied"));
                holder.reference.setValue(String.valueOf(snapshot.getChildrenCount()));
                if (snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){
                    holder.join.setText("Start Learning");
                    holder.join.setEnabled(true);
                    holder.flag=true;
                }else{
                    holder.flag=false;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(model.getSize())!=Integer.parseInt(model.getOccupied() )&& !TextUtils.equals(holder.join.getText(),"Start Learning")){
                    holder.joined.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue("joined").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Snackbar.make(holder.itemView.getRootView(),"Joined Successfully", BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(holder.itemView.getRootView(), Objects.requireNonNull(e.getMessage()), BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                holder.joined.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){
                           if (TextUtils.equals(holder.join.getText(),"Start Learning")){
                               FragmentActivity activity=(FragmentActivity)holder.itemView.getContext();
                               FragmentManager manager=activity.getSupportFragmentManager();
                               Bundle bundle=new Bundle();
                               bundle.putString("path",model.getPath());
                               Batch_videos batch_videos=new Batch_videos() ;
                               batch_videos.setArguments(bundle);
                               manager.beginTransaction().replace(R.id.homelayout,batch_videos).commit();
                           }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public Batch_adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.batch_layout,parent,false));
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        datastore db;
        SharedPreferences preferences;
        DatabaseReference reference,joined;
        MaterialButton join;
        TextView title,duration,time;
        Boolean flag=false;
        ImageView img;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            preferences= itemView.getContext().getSharedPreferences("essance", Context.MODE_PRIVATE);
            db=new datastore(preferences);
            join=itemView.findViewById(R.id.join);
            title=itemView.findViewById(R.id.title);
            duration=itemView.findViewById(R.id.coursr_duration);
            time= itemView.findViewById(R.id.time);
            img=itemView.findViewById(R.id.image);

        }
    }
}
