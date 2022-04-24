


package com.example.cm2305;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.Manifest;
import org.json.JSONObject;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.TextView;
import org.json.*;
import org.w3c.dom.Comment;

import android.view.LayoutInflater;

import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class CheckTrusted extends FragmentActivity {

    private ListView dataListView;

    private String email;

    ArrayList arrayList;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_list_item_1);
        arrayList =new ArrayList<String>();
        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://night-time-security-app-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference().child("Journeys");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            email = user.getEmail(); }


        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        dataListView = findViewById(android.R.id.list);
        dataListView.setAdapter(itemsAdapter);


        Query progress_User = myRef.orderByChild("ID_journeyStatus").equalTo(email+"In Progress");


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                //String string = dataSnapshot.getValue(String.class);
                final String CurrentCords = dataSnapshot.child("CurrentCords").getValue().toString();
                final String Name = dataSnapshot.child("Name").getValue().toString();
                final String dangerLevel = dataSnapshot.child("DangerLevel").getValue().toString();
                final String journeyStatus = dataSnapshot.child("journeyStatus").getValue().toString();
                final String ETA = dataSnapshot.child("ETA").getValue().toString();

                arrayList.add(Name);
                itemsAdapter.notifyDataSetChanged();




                dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        StringBuffer buffer = new StringBuffer();

                        buffer.append("CurrentCords: " + String.valueOf(CurrentCords) + "\n");
                        buffer.append("Name: " + Name + "\n");
                        buffer.append("DangerLevel: " + dangerLevel + "\n");
                        buffer.append("journeyStatus: " + journeyStatus + "\n");
                        buffer.append("ETA: " + ETA + "\n");
                        showMessage(Name + " " + CurrentCords, buffer.toString());
                    }


                });



            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                //String string = dataSnapshot.getValue(String.class);
                arrayList.clear();
                final String CurrentCords = dataSnapshot.child("CurrentCords").getValue().toString();
                final String Name = dataSnapshot.child("Name").getValue().toString();
                final String dangerLevel = dataSnapshot.child("DangerLevel").getValue().toString();
                final String journeyStatus = dataSnapshot.child("journeyStatus").getValue().toString();
                final String ETA = dataSnapshot.child("ETA").getValue().toString();

                arrayList.add(Name);
                itemsAdapter.notifyDataSetChanged();


                dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        StringBuffer buffer = new StringBuffer();

                        buffer.append("CurrentCords: " + String.valueOf(CurrentCords) + "\n");
                        buffer.append("Name: " + Name + "\n");
                        buffer.append("DangerLevel: " + dangerLevel + "\n");
                        buffer.append("journeyStatus: " + journeyStatus + "\n");
                        buffer.append("ETA: " + ETA + "\n");
                        showMessage(Name + " Add W3W " + CurrentCords, buffer.toString());
                    }


                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String commentKey = snapshot.getKey();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Comment movedComment = snapshot.getValue(Comment.class);
                String commentKey = snapshot.getKey();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();

            }
        };

        progress_User.addChildEventListener(childEventListener);

}

    public void showMessage(String Title,String Message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(Title);
        builder.setMessage(Message);
        builder.show();
    }

}