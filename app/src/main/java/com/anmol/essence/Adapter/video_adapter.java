package com.anmol.essence.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anmol.essence.Activity.Videoplayer;
import com.anmol.essence.Fragments.live_play_screen;
import com.anmol.essence.Fragments.video;
import com.anmol.essence.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.anmol.essence.model.video_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

public class video_adapter extends FirebaseRecyclerAdapter<video_model,video_adapter.Viewholder> {
    public video_adapter(@NonNull FirebaseRecyclerOptions<video_model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull video_adapter.Viewholder holder, int position, @NonNull video_model model) {
        holder.duration.setText(model.getDuration());
        holder.title.setText(model.getTitle());
        Picasso.get().load(model.getImg()).into(holder.img);
         holder.video.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                /* Intent intent=new Intent(holder.itemView.getContext(), Videoplayer.class);
                 intent.putExtra("url","https://www.youtube.com/watch?v=aDEckshPfus");
                 holder.itemView.getContext().startActivity(intent);*/
                 holder.ad.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                     @Override
                     public void onReceiveAd(@NonNull Ad ad2) {
                         holder.ad.showAd(new AdDisplayListener() {
                             @Override
                             public void adHidden(Ad ad) {
                                 Bundle bundle=new Bundle();
                                 bundle.putString("url",model.getUrl());
                                 video vi=new video();
                                 vi.setArguments(bundle);
                                 FragmentActivity activity=(FragmentActivity)holder.itemView.getContext();
                                 FragmentManager manager=activity.getSupportFragmentManager();
                                 manager.beginTransaction().replace(R.id.homelayout,vi).commit();
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
    }
    @NonNull
    @Override
    public video_adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_single_row,parent,false));
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ConstraintLayout video;
        TextView duration,title;
        StartAppAd ad;
        ImageView img;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            ad=new StartAppAd(itemView.getContext());
            video=itemView.findViewById(R.id.video);
            duration=itemView.findViewById(R.id.duration);
            title=itemView.findViewById(R.id.textView5);
            img=itemView.findViewById(R.id.imageView3);
        }
    }
}
