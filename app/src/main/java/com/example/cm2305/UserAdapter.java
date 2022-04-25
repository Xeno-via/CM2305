package com.example.cm2305;

import android.app.AppComponentFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


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

        public UserHolder(View itemView) {
            super(itemView);
            textViewName= itemView.findViewById(R.id.text_view_Name);
            textViewCurrentCoords = itemView.findViewById(R.id.text_view_CurrentCoords);
            textViewDangerLevel = itemView.findViewById(R.id.text_view_DangerLevel);
            textViewJourneyStatus = itemView.findViewById(R.id.text_view_journeyStatus);
        }
    }
}