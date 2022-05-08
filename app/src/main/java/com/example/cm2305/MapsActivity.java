package com.example.cm2305;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;
import org.json.JSONObject;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.TextView;
import org.json.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.hardware.SensorManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.seismic.ShakeDetector;


//volley
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
//
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.CancellationTokenSource;

import com.google.android.gms.tasks.OnSuccessListener;
import java.util.ArrayList;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import com.example.cm2305.databinding.ActivityMapsBinding;
import com.google.maps.android.PolyUtil;
import com.what3words.androidwrapper.What3WordsV3;
import com.what3words.javawrapper.request.Coordinates;
import com.what3words.javawrapper.response.ConvertTo3WA;





import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class  MapsActivity extends FragmentActivity implements OnMapReadyCallback, ShakeDetector.Listener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private RequestQueue mQueue;
    private Circle mCircle;
    public Marker destM;
    public static Polyline polyline;
    public DatabaseReference tasksRef;
    public static Marker currentlocMark;
    public static LatLng destCords;
    LocationRequest locationRequest = LocationRequest.create();
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallBack;
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    public static List<LatLng> decodedPath;
    private TinyDB tinydb;
    private AlertDialog dialog;
    double latitude;
    double longitude;
    private String trustedContactEmail;
    public String what3wordsReturn;
    private Spinner dropdown;

    private FloatingActionButton FABStart;
    private FloatingActionButton FABEnd;
    private FloatingActionButton SafeButton;
    private FloatingActionButton DangerButton;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog popupDialog;

    private int etaHour;
    private int etaMin;

    @Override public void hearShake() {
        if (polyline != null) {
            getDangerLevel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);

        // A non-zero delay is required for Android 12 and up (https://github.com/square/seismic/issues/24)
        int sensorDelay = SensorManager.SENSOR_DELAY_GAME;

        sd.start(sensorManager, sensorDelay);

        //setContentView(R.layout.activity_maps);
        //mTextViewResult = findViewById(R.id.textView);
        mQueue = Volley.newRequestQueue(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //What3Words(51.2423, -0.12423);


        tinydb = new TinyDB(this);


        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    updateVals(locationResult.getLastLocation());
                }
            }
        };

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch ((item.getTitle().toString())){

                case "Home":
                    //Intent home = new Intent(getApplicationContext(), MapsActivity.class);
                    //startActivity(home); //change activity
                    break;

                case "Friends":
                    Intent friends = new Intent(getApplicationContext(), TrustedActivity.class);
                    startActivity(friends); //change activity
                    break;

                case "Settings":
                    editSettingsDialog();
                    break;
            }

            return true;
        });

         DangerButton = findViewById(R.id.DangerButton);
         SafeButton = findViewById(R.id.SafeButton);
        DangerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DangerButton.setVisibility(View.INVISIBLE);
                SafeButton.setVisibility(View.VISIBLE);
                String dangerLevel = "Danger";
                setDangerLevel(dangerLevel);
                //editSettingsDialog();
            }
        });

        SafeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SafeButton.setVisibility(View.INVISIBLE);
                DangerButton.setVisibility(View.VISIBLE);
                String dangerLevel = "Safe";
                setDangerLevel(dangerLevel);
                //editSettingsDialog();
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();

                }
                else{
                    Toast.makeText(this,"Requires Permissions", Toast.LENGTH_SHORT).show();
                }
        }
    }
    @Override
    public void onBackPressed() {
        //Handle AlertDialog here.
        //Activity must not stop + Application must not close without user confirmation.
        exitByBackKey();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        currentlocMark = null;
        if (tasksRef != null)
        {   StopLocationUpdates();
            mCircle.remove();
            decodedPath = null;

        }
    }

    private void StartLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack, Looper.getMainLooper());
        updateGPS();
    }


    private void StopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private String GetCurrentUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            String name = user.getDisplayName();
            String email = user.getEmail();

            return email;


        } else {

            return null;
        }
    }

    private void updateGPS() { fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,  cancellationTokenSource.getToken()).addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateVals(location);

                }
            });
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);
            }
        }
    }


    private void updateVals (Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        What3Words(latitude,longitude);
        TextView what3wordsText = findViewById(R.id.what3WordsText);
        what3wordsText.setText(what3wordsReturn);

        LatLng coords = new LatLng(latitude, longitude);
        String LatLong = (latitude+""+","+longitude+"");

        Log.d("ADebugTag", "Value: " + '1');
        //TextView textView = (TextView) findViewById(R.id.textView);

        if (tasksRef != null)
        {
            tasksRef.child("CurrentCords").setValue(String.valueOf(coords));
            tasksRef.child("What3Words").setValue(String.valueOf(what3wordsReturn));

        }
        else
        {
           //do nothing
        }

        if (currentlocMark != null)
        {
            //currentlocMark = mMap.addMarker(new MarkerOptions().position(coords).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            currentlocMark.setPosition(new LatLng(latitude,longitude));
            //Log.d("ADebugTag", "Value: " + 'P');


        }
        else
        {
            currentlocMark = mMap.addMarker(new MarkerOptions().position(coords).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            //Log.d("ADebugTag", "Value: " + 'H');
        }


        FloatingActionButton stopButton = findViewById(R.id.floatingActionButton4);
        FloatingActionButton button = findViewById(R.id.floatingActionButton2);


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 18));
        if (MapsActivity.decodedPath == null) {
            //textView.setText("On Path: " + coords);

        } else {
            boolean hasDeviated = isPointOnPolyline(coords, new PolylineOptions().addAll(decodedPath), 20);
            //textView.setText("On Path: " + hasDeviated + " " + coords);
            float[] results = new float[2];
            Location.distanceBetween( location.getLatitude(), location.getLongitude(),
                    mCircle.getCenter().latitude, mCircle.getCenter().longitude, results);

            if (!hasDeviated) { //This occurs when user has deviated
                getDangerLevel();
            }

            // if current time exceeds eta alerts are sent
            int currentHour = LocalDateTime.now().getHour();
            int currentMin = LocalDateTime.now().getMinute();
            if (currentHour >= etaHour && currentMin >= etaMin) {
                buildDialog();
            }

            if( results[0] > mCircle.getRadius()  ){
                Toast.makeText(getBaseContext(), "Outside, distance from center: " + results[0] + " radius: " + mCircle.getRadius(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(), "Inside, distance from center: " + results[0] + " radius: " + mCircle.getRadius() , Toast.LENGTH_LONG).show();
                buildDialog2();

            }

        }


    }
    public void getDangerLevel() {

        tasksRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());

                }
                else {

                    String state = String.valueOf(task.getResult().child("DangerLevel").getValue());
                    dangerCheck(state);


                }
            }
        });


    }

    public void setDangerLevel(String dangerLevel) {
        tasksRef.child("DangerLevel").setValue(dangerLevel);
    }

    private void drawMarkerWithCircle(LatLng position){
        double radiusInMeters = 20.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = mMap.addCircle(circleOptions);


    }
    public void dangerCheck(String state) {

        //if( (dialog != null && !prevJourney.equals("In Progress")) ||  (dialog != null && (dialog.isShowing() || state != "Safe"))) {
        if ((dialog == null && state.equals("Safe")) || (dialog != null && !dialog.isShowing() && state.equals("Safe")) ){

            buildDialog();
        }
        else{
            //Do Nothing -- One already open
        }
    }

    private void buildDialog() {
        dialog = new AlertDialog.Builder(this)
                .setTitle("Danger Detected")
                .setMessage("We have detected anomalous behaviour, Are you Okay? ")
                .setPositiveButton("I'm Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing
                        String dangerLevel = "Safe";
                        setDangerLevel(dangerLevel);
                    }
                })
                .setNegativeButton("I'm in Danger", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing
                        String dangerLevel = "Danger";
                        setDangerLevel(dangerLevel);
                    }
                })
                .create();
        DialogTimeoutListener listener = new DialogTimeoutListener(tasksRef,1);
        dialog.setOnShowListener(listener);
        dialog.setOnDismissListener(listener);
        dialog.show();
    }

    private void buildDialog2() {
         dialog = new AlertDialog.Builder(this)
                .setTitle("End Journey")
                .setMessage("We have detected you have arrived at your destination ")
                .setPositiveButton("End Journey", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing
                        String dangerLevel = "Safe";
                        setDangerLevel(dangerLevel);
                        endJourney();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("I have not arrived", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Snooze functionality
                        dialog.dismiss();
                    }
                })
                .create();
        DialogTimeoutListener listener = new DialogTimeoutListener(tasksRef,4);
        dialog.setOnShowListener(listener);
        dialog.show();
    }

    private void exitByBackKey() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to Log Out? \n Doing so will cancel an In Progress Journey")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (tasksRef != null)
                        {

                            tasksRef.child("journeyStatus").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());

                                    }
                                    else {
                                        //Log.d("ADebugTag", "Value: " + String.valueOf(task.getResult().getValue()));
                                        if (String.valueOf(task.getResult().getValue()).equals("Finished")) {
                                           //Log.d("ADebugTag", "Value: " + 'W');
                                        }
                                        else{
                                            String journeyStatus = "Cancelled";
                                            //Log.d("ADebugTag", "Value: " + journeyStatus);
                                            tasksRef.child("journeyStatus").setValue(journeyStatus);
                                            String id_Journey = trustedContactEmail + journeyStatus;
                                            tasksRef.child("ID_journeyStatus").setValue(id_Journey);

                                        }

                                    }
                                }
                            });
                        }
                        else
                        {
                            //Do Nothing
                        }
                        StopLocationUpdates();
                        FirebaseAuth.getInstance().signOut();
                        restart();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //nothing
                    }
                })
                .show();
    }

    public void getSuggested(ArrayList<String> list) {
        tinydb.putListString("yourkey",list);
        AutoCompleteTextView editTextSearch = findViewById(R.id.actv);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.custom_list_item, R.id.text_view_list_item, list);
        editTextSearch.setAdapter(adapter);
        editTextSearch.setThreshold(1);



    }
    public void restart() {
        /*Context context = getApplicationContext();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);*/
        Intent mIntent = new Intent(this,FirebaseUIActivity.class);
        finishAffinity();
        startActivity(mIntent);
    }

    public static void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            System.err.format("IOException: %s%n", e);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection("Users").document(GetCurrentUser()).collection("Friends");
        dropdown = findViewById(R.id.spinner3);
        ArrayList<String> itemsList = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsList);
        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        itemsList.add(document.getString("Email"));
                    }
                    dropdown.setPrompt("Select Trusted Contact");
                    dropdown.setAdapter(adapter);
                }
            }
        });

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateGPS();

        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://night-time-security-app-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("Journeys");




        FloatingActionButton button =  findViewById(R.id.floatingActionButton2);
        //Button button3 = (Button) findViewById(R.id.button3);
         FABStart = findViewById(R.id.floatingActionButton2);
         FABEnd = findViewById(R.id.floatingActionButton4);
        //TextView textView = (TextView) findViewById(R.id.textView);
        AutoCompleteTextView editTextSearch = findViewById(R.id.actv);
        TextView What3WordsText = findViewById(R.id.what3WordsText);
        //What3Words();




        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                //editTextSearch.clearFocus();


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FABStart.setVisibility(s.toString().trim().length() > 0 ? View.VISIBLE : View.INVISIBLE);
                ArrayList<String> list = tinydb.getListString("yourkey");

                getSuggested(list);
            }});


        FloatingActionButton DangerButton = findViewById(R.id.DangerButton);
        FABStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {


                Object trustedContactEmailNullCheck = dropdown.getSelectedItem();
                if (trustedContactEmailNullCheck == null){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please Add a friend to share your journey with", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{

                Toast toast = Toast.makeText(getApplicationContext(), "Journey Started", Toast.LENGTH_SHORT);
                toast.show();
                FABStart.setVisibility(View.INVISIBLE);
                FABEnd.setVisibility(View.VISIBLE);
                StartLocationUpdates();
                button.setVisibility(View.INVISIBLE);
                DangerButton.setVisibility(View.VISIBLE);

                String LatLong = (latitude+""+","+longitude+"");
                String editTextValue = LatLong;
                String editTextValue2 = editTextSearch.getText().toString();


                addToSuggested(editTextValue2);


                //TextView textView = (TextView) findViewById(R.id.textView);
                String str_origin = "origin="+editTextValue;
                String[] arr = editTextValue2.split(" ");
                String strDestName = String.join("+", arr);// Destination of route
                String str_dest = "destination="+strDestName;
                String travel_Mode = "mode=walking";
                String Api = BuildConfig.MAPS_API_KEY;// Key
                String key = "key=" + Api; // Output format
                String parameters = str_origin+"&"+str_dest+"&"+travel_Mode+"&"+key;
                // Building the url to the web service
                String url = "https://maps.googleapis.com/maps/api/directions/json?"+parameters;

                //Log.d("ADebugTag", "Value: " + url);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                //textView.setText("Response: " + response.toString());

                                try {

                                    JSONObject obj = new JSONObject(response.toString());
                                    //textView.setText("Response: " + response.toString());
                                    String encoded_Route = obj.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");

                                    // gets duration in seconds from json array
                                    Integer duration = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getInt("value");

                                    Log.d("ADebugTag", "Value: " + duration);

                                    // converts duration to hours and minutes
                                    int minutes = (duration/60)%60;
                                    int hours = (duration/60)/60;

                                    // gets current hour and minute
                                    int currentMin = LocalDateTime.now().getMinute();
                                    int currentHour = LocalDateTime.now().getHour();

                                    // adds hours and minutes of journey duration to current time to get ETA
                                    if (minutes + currentMin > 59) {
                                        etaMin = (minutes+currentMin)-60;
                                        hours = hours+1;
                                    } else {
                                        etaMin = minutes+currentMin;
                                    }
                                    if (hours + currentHour > 23) {
                                        etaHour = (hours+currentHour)-24;
                                    } else {
                                        etaHour = hours+currentHour;
                                    }
                                    // converts eta to string and displays it on UI
                                    String hour = Integer.toString(etaHour);
                                    String minute = Integer.toString(etaMin);
                                    TextView uiETA = findViewById(R.id.uiETA);
                                    String stringETA = hour + ":" + minute;
                                    uiETA.setText(stringETA);

                                    //textView.setText("Response: " + encoded_Route);
                                    decodedPath = PolyUtil.decode(encoded_Route);
                                    polyline = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));

                                    String start_lat = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getString("lat");
                                    String start_long = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getString("lng");
                                    LatLng startCords = new LatLng( Double.parseDouble(start_lat), Double.parseDouble(start_long));

                                    String end_lat = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getString("lat");
                                    String end_long = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getString("lng");
                                    destCords = new LatLng( Double.parseDouble(end_lat), Double.parseDouble(end_long));
                                    destM = mMap.addMarker(new MarkerOptions().position(destCords).title("Destination Location"));
                                    trustedContactEmail = dropdown.getSelectedItem().toString();
                                    drawMarkerWithCircle(destCords);
                                    tasksRef = myRef.push();

                                    Journey journey = new Journey(startCords,destCords,GetCurrentUser(),trustedContactEmail,tasksRef);
                                    journey.add2Fire();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                tasksRef.child("DangerLevel").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // This method is called once with the initial value and again
                                        // whenever data at this location is updated.
                                        String value = dataSnapshot.getValue(String.class);
                                        if (value == "Danger"){
                                            dangerZone();}
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.w("TAG", "Failed to read value.", error.toException());
                                    }
                                });
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });

                mQueue.add(jsonObjectRequest);
            }}
        });

         SafeButton = findViewById(R.id.SafeButton);
        FABEnd.setOnClickListener(new View.OnClickListener() {public void onClick(View v) {

           endJourney();
        }

            });

       /* button3.setOnClickListener(new View.OnClickListener() {public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), TrustedActivity.class);
            startActivity(intent); //change activity

        }
        });*/

    }
    public void addToSuggested(String editTextValue2){
        ArrayList<String> list = tinydb.getListString("yourkey");
        boolean contains = list.contains(editTextValue2);


        if ((!contains)) {
            list.add(editTextValue2);
            tinydb.putListString("yourkey",list);

        } else {
            //Do Zilch
        }

    }

    public void clearSuggested(){
        ArrayList<String> list = tinydb.getListString("yourkey");
        list.clear();
        tinydb.putListString("yourkey",list);

    }

    public void endJourney(){
        Toast toast = Toast.makeText(getApplicationContext(), "Journey Ended", Toast.LENGTH_SHORT);
        toast.show();
        StopLocationUpdates();
        FABEnd.setVisibility(View.INVISIBLE);
        FABStart.setVisibility(View.VISIBLE);
        DangerButton.setVisibility(View.INVISIBLE);
        SafeButton.setVisibility(View.INVISIBLE);
        polyline.remove();
        mCircle.remove();
        destM.remove();
        String journeyStatus = "Finished";
        String id_Journey = trustedContactEmail + journeyStatus;
        tasksRef.child("ID_journeyStatus").setValue(id_Journey);
        tasksRef.child("journeyStatus").setValue(journeyStatus);
        dialog = null;
        polyline = null;


    }

    public void dangerZone(){
        dialog = new AlertDialog.Builder(this)
                .setTitle("Danger Detected")
                .setMessage("We have set your status as: In Danger")
                .setPositiveButton("I'm Now Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing
                        String dangerLevel = "Safe";
                        tasksRef.child("DangerLevel").setValue(dangerLevel);
                    }
                })

                .setNegativeButton("Call Emergency Services", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:999"));
                        startActivity(intent);

                    }
                })
              .setNeutralButton("Ask Friend to Call 999",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        String dangerLevel = "Please Call Emergency Services";
                        tasksRef.child("DangerLevel").setValue(dangerLevel);
                    }
                })
                .create();
        DialogTimeoutListener listener = new DialogTimeoutListener(tasksRef,2);
        dialog.setOnShowListener(listener);
        dialog.setOnDismissListener(listener);
        dialog.show();
        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button btnNeut = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        positiveButtonLL.gravity = Gravity.CENTER;
        btnPositive.setLayoutParams(positiveButtonLL);
        btnNegative.setLayoutParams(positiveButtonLL);
        btnNeut.setLayoutParams(positiveButtonLL);



    }

    public void What3Words(Double Lat,Double Long){
        What3WordsV3 wrapper = new What3WordsV3("ZQ0XPH3L", this);
        Observable.fromCallable(() -> wrapper.convertTo3wa(new Coordinates(Lat, Long)).execute())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccessful()) {
                        Log.i("MainActivity", String.format("3 word address: %s", result.getWords()));
                        what3wordsReturn = result.getWords();

                    } else {
                        Log.i("MainActivity", result.getError().getMessage());
                    }
                });



    }
    public static boolean isPointOnPolyline(LatLng point, PolylineOptions polyline, double tolerance) {
        return PolyUtil.isLocationOnPath(point, polyline.getPoints(), false, tolerance);
    }



    public class Journey {
        public LatLng startCords;
        public LatLng currCords;
        public LatLng destCords;
        public String userName;
        public String trustedUserName;
        public String dangerLevel;
        public String journeyStatus;
        public String ETA;
        public String What3Words;
        public String ID_js;
        public DatabaseReference tasksRef;



        public Journey(LatLng start, LatLng dest, String user, String trusted, DatabaseReference ref){
            startCords = start;
            currCords = start;
            destCords = dest;
            userName = user;
            trustedUserName = trusted;
            journeyStatus = "In Progress";
            dangerLevel = "Safe";
            ETA = "Dummy Data";
            tasksRef = ref;
            ID_js = trusted + journeyStatus;
            What3Words = what3wordsReturn;




        }
        public void add2Fire() {
            HashMap<String,String> journey=new HashMap<>();
            journey.put("Name",userName);
            journey.put("TrustedName",trustedUserName);
            journey.put("StartCords", String.valueOf(startCords));
            journey.put("EndCords", String.valueOf(destCords));
            journey.put("CurrentCords", String.valueOf(currCords));
            journey.put("DangerLevel", dangerLevel);
            journey.put("journeyStatus", journeyStatus);
            journey.put("ID_journeyStatus", ID_js);
            journey.put("ETA", ETA);
            journey.put("What3Words", What3Words);



            tasksRef.setValue(journey);
        }



    }
    private static class DialogTimeoutListener
            implements DialogInterface.OnShowListener, DialogInterface.OnDismissListener {
        private static final int AUTO_DISMISS_MILLIS = 1 * 60 * 1000;
        private CountDownTimer mCountDownTimer;
        private DatabaseReference taskRef;
        private Integer arg;

        public DialogTimeoutListener(DatabaseReference TaskRef, Integer argument){
            taskRef = TaskRef;  arg = argument;
        }

        @Override
        public void onShow(final DialogInterface dialog) {
            final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
            final CharSequence positiveButtonText = defaultButton.getText();

            mCountDownTimer = new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished > 60000) {
                        defaultButton.setText(String.format(
                                Locale.getDefault(), "%s (%d:%02d)",
                                positiveButtonText,
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished % 60000)
                        ));
                    } else {
                        defaultButton.setText(String.format(
                                Locale.getDefault(), "%s (%d)",
                                positiveButtonText,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                        ));
                    }
                }

                @Override
                public void onFinish() {
                    if (((AlertDialog) dialog).isShowing()) {

                        String dangerLevel = "Danger";
                        taskRef.child("DangerLevel").setValue(dangerLevel);
                        dialog.dismiss();
                    }
                }
            };
            if (arg == 1){
            String dangerLevel = "Warning";
            taskRef.child("DangerLevel").setValue(dangerLevel);
            mCountDownTimer.start();}
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            mCountDownTimer.cancel();
        }
    }

    public void editSettingsDialog(){
        // DEFAULT SETTINGS
        float defaultStrayingDistance = 50f;
        float defaultTimeAllowance = 10f;

        // Build Dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View settingsPopupView = getLayoutInflater().inflate(R.layout.popup_settings, null);

        dialogBuilder.setView(settingsPopupView);
        popupDialog = dialogBuilder.create();
        popupDialog.show();

        // Initialise buttons and fields on popup
        Button revertDefaultBtn = (Button)settingsPopupView.findViewById(R.id.revertToDefaultButton);
        Button saveSettingsBtn = (Button)settingsPopupView.findViewById(R.id.saveSettingsButton);
        EditText editStrayingDistance = settingsPopupView.findViewById(R.id.editStrayingDistance);
        EditText editTimeAllowance = settingsPopupView.findViewById(R.id.editTimeAllowance);
        Button buttonClear = (Button) settingsPopupView.findViewById(R.id.buttonClear);

        // Check if there are any preset settings
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userReference = db.collection("Users").document(GetCurrentUser());
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userDocument = task.getResult();
                    if (!userDocument.exists()){
                        db.collection("Users").document(GetCurrentUser()).set(new HashMap<String, Object>());
                    }
                    if (userDocument.getString("strayingDistance") != null) {
                        // if there is an entry for settings then show them in fields
                        editStrayingDistance.setText(userDocument.getString("strayingDistance"));
                        editTimeAllowance.setText(userDocument.getString("timeAllowance"));
                    }
                }
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSuggested();
                buttonClear.setVisibility(View.INVISIBLE);
            }
        });

        revertDefaultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete entry to revert to default settings
                Map<String,Object> updates = new HashMap<>();
                updates.put("strayingDistance", FieldValue.delete());
                updates.put("timeAllowance", FieldValue.delete());

                userReference.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("SETTINGS REVERT", "Settings have been deleted from database");
                    }
                });

                popupDialog.dismiss();
            }
        });

        saveSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save settings as entry
                Map<String, Object> updates = new HashMap<>();
                updates.put("strayingDistance", editStrayingDistance.getText().toString());
                updates.put("timeAllowance", editTimeAllowance.getText().toString());

                userReference.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("SETTINGS SET", "Settings have been altered to database.");
                    }
                });


                popupDialog.dismiss();
            }
        });
    }
}






