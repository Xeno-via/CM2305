package com.example.cm2305;


import static android.content.ContentValues.TAG;
import static com.example.cm2305.App.CHANNEL_1_ID;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.cm2305.App.CHANNEL_1_ID;

import java.util.ArrayList;
import java.util.List;

public class TrustedActivity extends AppCompatActivity {
    String email;
    private UserAdapter adapter;
    private  NotificationManagerCompat notificationManager;
    private DatabaseReference myRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_list_item_1);

        notificationManager = NotificationManagerCompat.from(this);



        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://night-time-security-app-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference().child("Journeys");


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            email = user.getEmail();
        }
        setUpRecyclerView();


        Query progress_User = myRef.orderByChild("TrustedName").equalTo(email);
        progress_User.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    String value = item.child("DangerLevel").getValue().toString();
                    if (value.equals("Danger")){
                        sendOnChannel1();
                    }

                //Log.d(TAG, "Value is: " + value);
                    }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



        FloatingActionButton addFriend = findViewById(R.id.button_add_note);
        addFriend.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), FriendRequestActivity.class);
                startActivity(intent); //change activity


            }
        });

        FloatingActionButton seeFriends = findViewById(R.id.button_see_Friends);
        seeFriends.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), FriendsListActivity.class);
                startActivity(intent); //change activity


            }
        });
    }

    public void sendOnChannel1() {
        String title = "Danger Alert!";
        String message = "A friend is in Danger";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
         }


    private void setUpRecyclerView() {
        Query progress_User = myRef.orderByChild("ID_journeyStatus").equalTo(email + "In Progress");

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(progress_User, User.class)
                .build();

        adapter = new UserAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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