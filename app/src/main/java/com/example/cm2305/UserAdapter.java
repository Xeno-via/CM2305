package com.example.cm2305;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.app.Activity;
import android.app.AppComponentFactory;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


import android.app.Application;



import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

public class UserAdapter extends FirebaseRecyclerAdapter<User, UserAdapter.UserHolder> {



    public UserAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
    }




    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
        holder.textViewName.setText(model.getName());
        holder.textViewCurrentCoords.setText(model.getCurrentCords());
        holder.textViewDangerLevel.setText(String.valueOf(model.getDangerLevel()));
        holder.textViewJourneyStatus.setText(String.valueOf(model.getJourneyStatus()));
        holder.textViewWhat3Words.setText(String.valueOf(model.getWhat3Words()));


    }




    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,
                parent, false);
        return new UserHolder(v);
    }

    class UserHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewCurrentCoords;
        TextView textViewDangerLevel;
        TextView textViewJourneyStatus;
        TextView textViewWhat3Words;

        public UserHolder(View itemView) {
            super(itemView);
            textViewName= itemView.findViewById(R.id.text_view_Name);
            textViewCurrentCoords = itemView.findViewById(R.id.text_view_CurrentCoords);
            textViewDangerLevel = itemView.findViewById(R.id.text_view_DangerLevel);
            textViewJourneyStatus = itemView.findViewById(R.id.text_view_journeyStatus);
            textViewWhat3Words= itemView.findViewById(R.id.text_view_What3Words);
        }
    }


}


