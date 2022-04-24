package com.example.cm2305;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;



import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;


import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

public class FriendAdapter extends FirestoreRecyclerAdapter<FriendRequests, FriendAdapter.FriendHolder> {
    private OnItemClickListener listener;

    public FriendAdapter(@NonNull FirestoreRecyclerOptions<FriendRequests> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendHolder holder, int position, @NonNull FriendRequests model) {
        holder.textViewName.setText(model.getName());
        holder.textViewEmail.setText(model.getEmail());

    }




    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item,
                parent, false);
        return new FriendHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class FriendHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewEmail;


        public FriendHolder(View itemView) {
            super(itemView);
            textViewName= itemView.findViewById(R.id.text_view_Name);
            textViewEmail = itemView.findViewById(R.id.text_view_Email);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }


    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}