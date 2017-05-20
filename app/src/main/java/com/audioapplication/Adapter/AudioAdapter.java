package com.audioapplication.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.audioapplication.Models.AudioPayload;
import com.audioapplication.Models.Product;
import com.audioapplication.R;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    List<AudioPayload> audioList;
    Activity activity;
    LayoutInflater mInflater;

    public AudioAdapter(List<AudioPayload> productList, Activity activity) {
        this.audioList = productList;
        this.activity = activity;
        mInflater = ((LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    }


    @Override
    public AudioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.single_audio_view, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final AudioAdapter.ViewHolder holder, int position) {
        AudioPayload audioPayload=audioList.get(position);
        holder.file_name.setText(audioPayload.getFilename());
        String time = audioPayload.getCreated().substring(11,16);
        Date dateObj;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            dateObj = sdf.parse(time);
            holder.created_date.setText("Created at :" +audioPayload.getCreated().substring(0,10) +" - "+new SimpleDateFormat("K:mm a").format(dateObj));
        }catch (Exception e){
            e.printStackTrace();
            holder.created_date.setText("Created at : " +audioPayload.getCreated().substring(0,10));
        }
    }

    public void add(AudioPayload payload){
        audioList.add(payload);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }


     class ViewHolder extends RecyclerView.ViewHolder {
        TextView file_name;
        TextView created_date;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            file_name = (TextView) itemLayoutView.findViewById(R.id.file_name);
            created_date = (TextView) itemLayoutView.findViewById(R.id.created_date);
         }
    }
}