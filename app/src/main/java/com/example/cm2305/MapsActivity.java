package com.example.cm2305;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
    private Circle mCircle;
    public Marker destM;
    public static Polyline polyline;
    public static Marker currentlocMark;
    public static LatLng destCords;
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

        locationRequest.setInterval(5000);
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

        EditText simpleEditText = (EditText) findViewById(R.id.simpleEditText);

        Log.d("ADebugTag", "Value: " + '1');
        simpleEditText.setText(latitude+""+","+longitude+"",TextView.BufferType.EDITABLE);
        simpleEditText.setEnabled(false);
        TextView textView = (TextView) findViewById(R.id.textView);

        if (currentlocMark != null)
        {
            currentlocMark.setPosition(new LatLng(latitude,longitude));
        }
        else
        {
            currentlocMark = mMap.addMarker(new MarkerOptions().position(coords).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        }


        Button stopButton = (Button) findViewById(R.id.button2);
        Button button = (Button) findViewById(R.id.button_send);


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 18));
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



        Button stopButton = (Button) findViewById(R.id.button2);
        Button button = (Button) findViewById(R.id.button_send);
        stopButton.setVisibility(View.INVISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartLocationUpdates();
                button.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                EditText simpleEditText = (EditText) findViewById(R.id.simpleEditText);

                EditText simpleEditText2 = (EditText) findViewById(R.id.simpleEditText2);
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


                Log.d("ADebugTag", "Value: " + url);



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

                                    String end_lat = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getString("lat");
                                    String end_long = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getString("lng");
                                    destCords = new LatLng( Double.parseDouble(end_lat), Double.parseDouble(end_long));
                                    destM = mMap.addMarker(new MarkerOptions().position(destCords).title("Destination Location"));
                                    drawMarkerWithCircle(destCords);

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




}

