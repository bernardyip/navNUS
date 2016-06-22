package com.navnus.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.navnus.R;
import com.navnus.entity.GeoCoordinate;
import com.navnus.entity.Map;
import com.navnus.entity.Vertex;

import java.util.LinkedList;

import edu.princeton.cs.algs4.DirectedEdge;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int from;
    private int to;

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
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


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

        //Center camera to show all markers
        builder.include(startMarker.getPosition());
        builder.include(endMarker.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        //googleMap.animateCamera(cu); //If wanna have animation
        googleMap.moveCamera(cu);
    }
}
