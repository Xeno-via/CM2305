package com.example.cm2305;

import static android.content.ContentValues.TAG;

import android.app.AppComponentFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendRequestActivity extends AppCompatActivity {
    String email;
    private FriendAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_list_item_1);



        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();




        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            email = user.getEmail();
        }
        setUpRecyclerView();


        FloatingActionButton addFriend = findViewById(R.id.button_add_note);
        addFriend.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


            }
        });
    }

    private void setUpRecyclerView() {
        CollectionReference users = db.collection("Users").document(email).collection("FriendRequests");


        FirestoreRecyclerOptions<FriendRequests> options = new FirestoreRecyclerOptions.Builder<FriendRequests>()
                .setQuery(users, FriendRequests.class)
                .build();

        adapter = new FriendAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            FriendRequests friend = documentSnapshot.toObject(FriendRequests.class);
            //String id = documentSnapshot.getId();
            //String path = documentSnapshot.getReference().getPath();
            String name = friend.getName();
            String emailofFriend = friend.getEmail();
            //Toast.makeText(FriendRequestActivity.this,
                  //  "Position: " + position + " ID: " + name, Toast.LENGTH_SHORT).show();
            Map<String, Object> docData = new HashMap<>();
            docData.put("Name", name);
            docData.put("Email", emailofFriend);
            db.collection("Users").document(email).collection("Friends").add(docData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    Toast.makeText(FriendRequestActivity.this,
                            name + " -- Has been added as a friend", Toast.LENGTH_SHORT).show();
                                adapter.deleteItem(position);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });


        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }




}