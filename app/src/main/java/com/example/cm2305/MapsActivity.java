package com.example.cm2305;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;
import android.Manifest;
import org.json.JSONObject;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.TextView;
import org.json.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.example.cm2305.databinding.ActivityMapsBinding;
import com.google.maps.android.PolyUtil;
import com.what3words.androidwrapper.What3WordsV3;
import com.what3words.javawrapper.request.Coordinates;
import com.what3words.javawrapper.response.ConvertTo3WA;


import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class  MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private TextView mTextViewResult;
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
    public static final String TAG = "Error";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //binding = ActivityMapsBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        setContentView(R.layout.activity_maps);
        mTextViewResult = findViewById(R.id.textView);
        mQueue = Volley.newRequestQueue(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        What3Words(51.2423, -0.12423);

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

            String journeyStatus = "Cancelled";
            tasksRef.child("journeyStatus").setValue(journeyStatus);
        }


    }

    private void StartLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack, Looper.getMainLooper());
        updateGPS();
    }


    private void StopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
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
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng coords = new LatLng(latitude, longitude);



        EditText simpleEditText = (EditText) findViewById(R.id.simpleEditText);

        Log.d("ADebugTag", "Value: " + '1');
        simpleEditText.setText(latitude+""+","+longitude+"",TextView.BufferType.EDITABLE);
        simpleEditText.setEnabled(false);
        TextView textView = (TextView) findViewById(R.id.textView);

        if (tasksRef != null)
        {
            tasksRef.child("CurrentCords").setValue(String.valueOf(coords));

        }
        else
        {
           int donothing = 1;
        }

        if (currentlocMark != null)
        {
            //currentlocMark = mMap.addMarker(new MarkerOptions().position(coords).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            currentlocMark.setPosition(new LatLng(latitude,longitude));
            Log.d("ADebugTag", "Value: " + 'P');


        }
        else
        {
            currentlocMark = mMap.addMarker(new MarkerOptions().position(coords).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            Log.d("ADebugTag", "Value: " + 'H');
        }


        Button stopButton = (Button) findViewById(R.id.button2);
        Button button = (Button) findViewById(R.id.button_send);


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 18));
        if (MapsActivity.decodedPath == null) {
            textView.setText("On Path: " + coords);

        } else {
            boolean hasDeviated = isPointOnPolyline(coords, new PolylineOptions().addAll(decodedPath), 20);
            textView.setText("On Path: " + hasDeviated + " " + coords);
            float[] results = new float[2];
            Location.distanceBetween( location.getLatitude(), location.getLongitude(),
                    mCircle.getCenter().latitude, mCircle.getCenter().longitude, results);

            if( results[0] > mCircle.getRadius()  ){
                Toast.makeText(getBaseContext(), "Outside, distance from center: " + results[0] + " radius: " + mCircle.getRadius(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(), "Inside, distance from center: " + results[0] + " radius: " + mCircle.getRadius() , Toast.LENGTH_LONG).show();
                StopLocationUpdates();
                stopButton.setVisibility(View.INVISIBLE);
                button.setVisibility(View.VISIBLE);
                polyline.remove();
                mCircle.remove();
                destM.remove();
                String journeyStatus = "Finished";
                tasksRef.child("journeyStatus").setValue(journeyStatus);

            }

        }


    }

    private void drawMarkerWithCircle(LatLng position){
        double radiusInMeters = 20.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = mMap.addCircle(circleOptions);


    }

    private void exitByBackKey() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to Log Out? \n Doing so will cancel an In Progress Journey")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        String journeyStatus = "Cancelled";
                        tasksRef.child("journeyStatus").setValue(journeyStatus);
                        Context context = getApplicationContext();
                        PackageManager packageManager = context.getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
                        ComponentName componentName = intent.getComponent();
                        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                        context.startActivity(mainIntent);
                        Runtime.getRuntime().exit(0);


                        //close();
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

        //Log.d("ADebugTag", "Value: " + editTextValue);
        //String[] arr = editTextValue.split(",");
        Log.d("ADebugTag", "Value: " + '2');

        //LatLng currentLoc = new LatLng(, Double.parseDouble(arr[1]));
        //mMap.addMarker(new MarkerOptions().position(currentLoc).title("Current Location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));

        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://night-time-security-app-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("Journeys");

        Button stopButton = (Button) findViewById(R.id.button2);
        Button button = (Button) findViewById(R.id.button_send);
        Button button3 = (Button) findViewById(R.id.button3);
        stopButton.setVisibility(View.INVISIBLE);
        TextView textView = (TextView) findViewById(R.id.textView);
        EditText simpleEditText2 = (EditText) findViewById(R.id.simpleEditText2);

        simpleEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                button.setVisibility(s.toString().trim().length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }});


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartLocationUpdates();
                button.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                EditText simpleEditText = (EditText) findViewById(R.id.simpleEditText);



                String editTextValue = simpleEditText.getText().toString();
                String editTextValue2 = simpleEditText2.getText().toString();
                TextView textView = (TextView) findViewById(R.id.textView);




                String str_origin = "origin="+editTextValue;

                // Destination of route


                String[] arr = editTextValue2.split(" ");

                String strDestName = String.join("+", arr);


                String str_dest = "destination="+strDestName;



                String travel_Mode = "mode=walking";

                // Key
                String Api = BuildConfig.MAPS_API_KEY;
                String key = "key=" + Api;

                // Output format
                String output = "json?";

                // Building the parameters to the web service
                String parameters = str_origin+"&"+str_dest+"&"+travel_Mode+"&"+key;



                // Building the url to the web service
                String url = "https://maps.googleapis.com/maps/api/directions/json?"+parameters;


                //Log.d("ADebugTag", "Value: " + url);

                //myRef.setValue("Hello, World!");



                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                //textView.setText("Response: " + response.toString());

                                try {
                                    JSONObject obj = new JSONObject(response.toString());
                                    //textView.setText("Response: " + response.toString());
                                    String encoded_Route = obj.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
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
                                    drawMarkerWithCircle(destCords);
                                    tasksRef = myRef.push();
                                    Journey journey = new Journey(startCords,destCords,"UserTest","TrustedTest",tasksRef);

                                    journey.add2Fire();



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });

                mQueue.add(jsonObjectRequest);



            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {public void onClick(View v) {

            StopLocationUpdates();
            stopButton.setVisibility(View.INVISIBLE);
            button.setVisibility(View.VISIBLE);
            polyline.remove();
            mCircle.remove();
            destM.remove();
            String journeyStatus = "Finished";
            tasksRef.child("journeyStatus").setValue(journeyStatus);

        }




            });

        button3.setOnClickListener(new View.OnClickListener() {public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), CheckTrusted.class);
            startActivity(intent); //change activity

        }
        });




    }


    public void What3Words(Double Lat,Double Long){
        What3WordsV3 wrapper = new What3WordsV3("ZQ0XPH3L", this);
        Observable.fromCallable(() -> wrapper.convertTo3wa(new Coordinates(Lat, Long)).execute())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccessful()) {
                        Log.i("MainActivity", String.format("3 word address: %s", result.getWords()));

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
        public DatabaseReference tasksRef;
        public int journeyID;


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
            journey.put("ETA", ETA);



            tasksRef.setValue(journey);
        }



    }


}

