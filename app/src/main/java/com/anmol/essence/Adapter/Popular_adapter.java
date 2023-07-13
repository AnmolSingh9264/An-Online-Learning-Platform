package com.anmol.essence.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anmol.essence.Fragments.Details;
import com.anmol.essence.R;
import com.anmol.essence.database.datastore;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.anmol.essence.model.popular_course_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class Popular_adapter extends FirebaseRecyclerAdapter<popular_course_model,Popular_adapter.Viewholder> {
    public Popular_adapter(@NonNull FirebaseRecyclerOptions<popular_course_model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Popular_adapter.Viewholder holder, int position, @NonNull popular_course_model model) {
      holder.title.setText(model.getTitle());
      holder.rate.setText(model.getRate());
      holder.hour.setText(model.getHours());
      holder.langauge.setText(model.getLangauge());
      Picasso.get().load(model.getImage()).into(holder.image);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.db.store("title",model.getTitle());
                FragmentActivity activity=(FragmentActivity)holder.itemView.getContext();
                FragmentManager manager=activity.getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.homelayout,new Details()).commit();
            }
        });
    }

    @NonNull
    @Override
    public Popular_adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_course_layout,parent,false));
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title,rate,hour,langauge;
        ConstraintLayout cardView;
        datastore db;
        SharedPreferences preferences;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.imageView);
            title=itemView.findViewById(R.id.textView);
            rate=itemView.findViewById(R.id.rate    );
            hour=itemView.findViewById(R.id.hour);
            langauge=itemView.findViewById(R.id.lang);
            cardView = itemView.findViewById(R.id.parent);
            preferences=itemView.getContext().getSharedPreferences("essance", Context.MODE_PRIVATE);
            db=new datastore(preferences);
        }
    }
}
