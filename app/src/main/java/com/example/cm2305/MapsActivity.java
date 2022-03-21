package com.example.cm2305;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;
import org.json.JSONObject;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.TextView;
import org.json.*;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.android.gms.tasks.OnSuccessListener;

import com.example.cm2305.databinding.ActivityMapsBinding;
import com.google.maps.android.PolyUtil;
import com.what3words.androidwrapper.What3WordsV3;
import com.what3words.javawrapper.request.Coordinates;
import com.what3words.javawrapper.response.ConvertTo3WA;


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

    LocationRequest locationRequest = LocationRequest.create();
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallBack;
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    public static List<LatLng> decodedPath;

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

        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
        updateGPS();
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
        TextView textView = (TextView) findViewById(R.id.textView);
        if (MapsActivity.decodedPath == null) {
            textView.setText("On Path: " + coords);

        } else {
            boolean hasDeviated = isPointOnPolyline(coords, new PolylineOptions().addAll(decodedPath), 20);
            textView.setText("On Path: " + hasDeviated + " " + coords);

        }
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));




        CameraPosition PACIFIC_CAMERA = new CameraPosition.Builder()
                .target(new LatLng(34.1358593, -117.922066)).zoom(10.0f).bearing(0).tilt(0).build();

        String str_origin = "origin="+"34.1358593,-117.922066";

        // Destination of route
        String str_dest = "destination="+"+34.1358593,-118.3511633";

        String travel_Mode = "mode=walking";

        // Key
        String key = "key=" + "AIzaSyD3BajDg2sSEYyflYBzFRHwmJjNM-xfM7A";

        // Building the parameters to the web service
        String parameters = str_origin+"&amp;"+str_dest+"&amp;"+travel_Mode+"&amp;"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood&key=AIzaSyD3BajDg2sSEYyflYBzFRHwmJjNM-xfM7A";

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(PACIFIC_CAMERA));

        Button button = (Button) findViewById(R.id.button_send);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartLocationUpdates();
            }
        });



        TextView textView = (TextView) findViewById(R.id.textView);
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
                            mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
                            boolean hasDeviated = isPointOnPolyline(new LatLng(33.821700, -117.922683), new PolylineOptions().addAll(decodedPath), 20);
                            mMap.addMarker(new MarkerOptions().position(new LatLng(33.821700, -117.922683)).title("Marker"));
                            //textView.setText("On Path: " + hasDeviated);

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

        //StartLocationUpdates();






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




}

