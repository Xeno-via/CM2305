package com.example.cm2305;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;



public class FriendsListActivity extends AppCompatActivity {
    String Useremail;
    private FriendAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friendslist);



        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();




        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            Useremail = user.getEmail();
        }
        setUpRecyclerView();

        EditText friendEmail = findViewById(R.id.simpleEditText);

        FloatingActionButton addFriend = findViewById(R.id.button_add_note);
        addFriend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String emailFriend = friendEmail.getText().toString();
                if (!emailFriend.equals("")){
                    DocumentReference docFriend = db.collection("Users").document(emailFriend);
                    docFriend.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    CollectionReference addFriendRequest = docFriend.collection("FriendRequests");
                                    Query alreadySent =  addFriendRequest.whereEqualTo("Email", Useremail);

                                    alreadySent.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                Integer i = 0;
                                                for (DocumentSnapshot document : task.getResult()) { i+=1; }

                                                if (i >=1) {
                                                    Toast toast = Toast.makeText(getApplicationContext(), "You have Already Sent a Friend Request to this user", Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    return;
                                                }
                                                else{
                                                    CollectionReference checkAlreadyFriend = docFriend.collection("Friends");
                                                    Query alreadyFriend =  checkAlreadyFriend.whereEqualTo("Email", Useremail);
                                                    alreadyFriend.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {

                                                                Integer i = 0;
                                                                for (DocumentSnapshot document : task.getResult()) {
                                                                    i += 1;
                                                                }

                                                                if (i >= 1) {
                                                                    Toast toast = Toast.makeText(getApplicationContext(), "You have Already Friends with this User", Toast.LENGTH_SHORT);
                                                                    toast.show();
                                                                    return;
                                                                }

                                                                else {
                                                                    String nameofUser = "Name"; //TODO add name
                                                                    String emailofUser = Useremail;

                                                                    Map<String, Object> docDataUser = new HashMap<>();
                                                                    docDataUser.put("Name", nameofUser);
                                                                    docDataUser.put("Email", emailofUser);
                                                                    addFriendRequest.add(docDataUser);
                                                                    Toast toast = Toast.makeText(getApplicationContext(), "Friend Request Sent!", Toast.LENGTH_SHORT);
                                                                    toast.show();
                                                                    friendEmail.getText().clear();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }

                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });


                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            } else {
                                Log.d(TAG, "Failed with: ", task.getException());
                            }
                        }
                    });

                }

            }
        });
    }

    private void setUpRecyclerView() {
        CollectionReference users = db.collection("Users").document(Useremail).collection("Friends");


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
                adapter.getItem(viewHolder.getAbsoluteAdapterPosition());



                String friendemail = adapter.getItem(viewHolder.getAbsoluteAdapterPosition()).getEmail();

                CollectionReference friendDel = db.collection("Users").document(friendemail).collection("Friends");
                Query query = friendDel.whereEqualTo("Email", Useremail);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String ref = document.getId();
                                DocumentReference docRef = friendDel.document(ref);
                                docRef.delete();


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

                adapter.deleteItem(viewHolder.getAdapterPosition()); //deletefrom user document

            }
        }).attachToRecyclerView(recyclerView);


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