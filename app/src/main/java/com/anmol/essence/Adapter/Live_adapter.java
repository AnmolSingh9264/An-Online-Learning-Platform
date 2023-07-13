package com.anmol.essence.Adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anmol.essence.Fragments.live_play_screen;
import com.anmol.essence.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.anmol.essence.model.live_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

public class Live_adapter extends  FirebaseRecyclerAdapter<live_model, Live_adapter.Viewholder> {
    public Live_adapter(@NonNull FirebaseRecyclerOptions<live_model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Live_adapter.Viewholder holder, int position, @NonNull live_model model) {
                holder.join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.ad.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                            @Override
                            public void onReceiveAd(@NonNull Ad ad2) {
                                holder.ad.showAd(new AdDisplayListener() {
                                    @Override
                                    public void adHidden(Ad ad) {
                                        Bundle bundle=new Bundle();
                                        bundle.putString("video",model.getVideo());
                                        live_play_screen screen=new live_play_screen();
                                        screen.setArguments(bundle);
                                        FragmentActivity activity=(FragmentActivity) holder.itemView.getContext();
                                        FragmentManager manager=activity.getSupportFragmentManager();
                                        manager.beginTransaction().replace(R.id.homelayout,screen)
                                                .commit();
                                    }

                                    @Override
                                    public void adDisplayed(Ad ad) {
                                        Toast.makeText(holder.itemView.getContext(), "Video will be started after ad", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void adClicked(Ad ad) {

                                    }

                                    @Override
                                    public void adNotDisplayed(Ad ad) {

                                    }
                                });
                            }

                            @Override
                            public void onFailedToReceiveAd(@Nullable Ad ad) {
                                Toast.makeText(holder.itemView.getContext(), ad.errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                holder.time.setText(model.getTime());
                holder.sub.setText(model.getSubject());
                holder.chapter.setText(model.getChapter());
        Picasso.get().load(model.getImg()).into(holder.img);
    }

    @NonNull
    @Override
    public Live_adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.live_layout_raw,parent,false));
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        Button join;
        StartAppAd ad;
        TextView sub,chapter,time;
        ImageView img;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            ad=new StartAppAd(itemView.getContext());
            join=itemView.findViewById(R.id.btn_join);
            sub=itemView.findViewById(R.id.subject);
            chapter=itemView.findViewById(R.id.chapter);
            time=itemView.findViewById(R.id.time);
            img=itemView.findViewById(R.id.imageView7);
        }
    }
    public void showad(){

    }
}
