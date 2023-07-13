package com.anmol.essence.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anmol.essence.R;
import com.anmol.essence.model.doubt_model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class doubt_adapter extends FirebaseRecyclerAdapter<doubt_model,doubt_adapter.Viewholder> {

    public doubt_adapter(@NonNull FirebaseRecyclerOptions<doubt_model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull doubt_adapter.Viewholder holder, int position, @NonNull doubt_model model) {

    }

    @NonNull
    @Override
    public doubt_adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.doubt_answer_layout,parent,false));
    }

    public class Viewholder  extends RecyclerView.ViewHolder{
        TextView name,des;
        ImageView ans;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.textView6);
            des=itemView.findViewById(R.id.textView8);
            ans=itemView.findViewById(R.id.img);
        }
    }
}
