package com.anmol.essence.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anmol.essence.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.anmol.essence.model.Class_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class Class_adapter extends FirebaseRecyclerAdapter<Class_model, Class_adapter.Viewholder> {
    public Class_adapter(@NonNull FirebaseRecyclerOptions<Class_model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Viewholder holder, int position, @NonNull Class_model model) {

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.classes,parent,false));
    }


    public class Viewholder extends RecyclerView.ViewHolder {
        public Viewholder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
