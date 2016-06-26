package com.navnus.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.navnus.R;
import com.navnus.entity.GeoCoordinate;
import com.navnus.entity.Map;
import com.navnus.entity.Vertex;
import com.navnus.util.GPSTracker;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import edu.princeton.cs.algs4.DirectedEdge;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int from;
    private int to;
    private GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Get the src/dest
        from = -1;
        to = -1;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            from = extras.getInt("from");
            to = extras.getInt("to");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); //This calls onMapRead when it is done

        /*
        //Get GPS and draw
        gps = new GPSTracker(this);
        // check if GPS enabled
        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        */
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        //Get the src and dest
        LinkedList<DirectedEdge> path = Map.getPath(from, to);
        Vertex start = Map.getVertex(from);
        Vertex end = Map.getVertex(to);

        // Add markers
        Marker startMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(start.coordinate.latitude, start.coordinate.longitude)).title(start.name));
        Marker endMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(end.coordinate.latitude, end.coordinate.longitude)).title(end.name));


        //Draw the route
        if (path != null) {
            for (DirectedEdge edge : path) {
                PolylineOptions route = new PolylineOptions();
                for (GeoCoordinate coord : edge.coordinates) {
                    route.add(new LatLng(coord.latitude, coord.longitude));
                }
                mMap.addPolyline(route);
            }
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //Center camera to show all markers
        builder.include(startMarker.getPosition());
        builder.include(endMarker.getPosition());
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.moveCamera(cu);
    }

}