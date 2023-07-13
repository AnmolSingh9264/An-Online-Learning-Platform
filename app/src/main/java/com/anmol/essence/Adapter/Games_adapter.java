package com.anmol.essence.Adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anmol.essence.Fragments.Game;
import com.anmol.essence.R;
import com.anmol.essence.model.games_model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

public class Games_adapter extends FirebaseRecyclerAdapter<games_model,Games_adapter.Viewholder> {
    public Games_adapter(@NonNull FirebaseRecyclerOptions<games_model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Games_adapter.Viewholder holder, int position, @NonNull games_model model) {
        Picasso.get().load(model.getImg()).into(holder.thumb);
            holder.game.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.ad.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                        @Override
                        public void onReceiveAd(@NonNull Ad ad) {
                            holder.ad.showAd(new AdDisplayListener() {
                                @Override
                                public void adHidden(Ad ad) {
                                    Bundle bundle=new Bundle();
                                    bundle.putString("data",model.getUrl());
                                    Game game=new Game();
                                    game.setArguments(bundle);
                                    FragmentActivity activity=(FragmentActivity) holder.itemView.getContext();
                                    FragmentManager manager=activity.getSupportFragmentManager();
                                    manager.beginTransaction().replace(R.id.homelayout,game).commit();
                                }

                                @Override
                                public void adDisplayed(Ad ad) {
                                    Toast.makeText(holder.itemView.getContext(), "Game will be started after ad", Toast.LENGTH_SHORT).show();
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
    }

    @NonNull
    @Override
    public Games_adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.games_layout,parent,false));
    }
    public class Viewholder extends RecyclerView.ViewHolder {
        RelativeLayout game;
        StartAppAd ad;
        ImageView thumb;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            game=itemView.findViewById(R.id.game);
            ad=new StartAppAd(itemView.getContext());
            thumb=itemView.findViewById(R.id.thumbnail);
        }
    }
}
