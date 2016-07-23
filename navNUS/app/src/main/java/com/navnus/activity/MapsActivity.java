package com.navnus.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.navnus.R;
import com.navnus.entity.GeoCoordinate;
import com.navnus.entity.Map;
import com.navnus.entity.Vertex;
import com.navnus.util.GPSTracker;

import java.util.LinkedList;

import edu.princeton.cs.algs4.DirectedEdge;

public class MapsActivity extends Activity {
    //private GoogleMap mMap;
    private Vertex from;
    private Vertex to;
    //private GPSTracker gps;
    private MapboxMap map;
    private MapView mapView;
    private LocationManager locationManager;
    private LocationListener listener;
    private Context context;
    private TextView distanceLeftTV, instructionsTV;

    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = this;

        distanceLeftTV = (TextView)findViewById(R.id.distanceLeftTV);
        instructionsTV = (TextView)findViewById(R.id.instructionsTV);

        //Get the src/dest
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            from = Map.getVertex(extras.getInt("from"));
            to = Map.getVertex(extras.getInt("to"));
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapView= (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                map.isMyLocationEnabled();
                //Get the src and dest
                LinkedList<DirectedEdge> path = Map.getPath(from.id, to.id);

                // Add markers
                MarkerViewOptions startMarker = new MarkerViewOptions().position(new LatLng(from.coordinate.latitude, from.coordinate.longitude)).title(from.name);
                MarkerViewOptions endMarker = new MarkerViewOptions().position(new LatLng(to.coordinate.latitude, to.coordinate.longitude)).title(to.name);
                map.addMarker(startMarker);
                map.addMarker(endMarker);

                //Draw the route
                if (path != null) {
                    for (DirectedEdge edge : path) {
                        PolylineOptions route = new PolylineOptions();
                        for (GeoCoordinate coord : edge.coordinates) {
                            route.add(new LatLng(coord.latitude, coord.longitude));
                        }
                        map.addPolyline(route);
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

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                map.moveCamera(cu);

                //Start GPS Tracking
                /********** get Gps location service LocationManager object ***********/
                Criteria crit = new Criteria();
                crit.setAccuracy(Criteria.ACCURACY_LOW);
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                final String bestProvider = locationManager.getBestProvider(crit, false);
                listener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        // TODO Auto-generated method stub
                        String str = "Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude();
                        //Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
                        Log.w("NAVNUS", str);
                        double distance = estimateDistance(location.getLatitude(), location.getLongitude(), to.coordinate.latitude, to.coordinate.longitude);
                        distanceLeftTV.setText(Math.round(distance) + "m left to your destination");
                        Log.w("NAVNUS", "Remaining Distance : " + distance);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        // TODO Auto-generated method stub
                        //Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
                        Log.w("NAVNUS", "GPS OFF");
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        // TODO Auto-generated method stub
                        //Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
                        Log.w("NAVNUS", "GPS ON");
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        // TODO Auto-generated method stubx
                    }
                };
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getBaseContext(), "No Location!", Toast.LENGTH_SHORT);
                }
                locationManager.requestLocationUpdates(bestProvider, 0, 0, listener);
                map.setMyLocationEnabled(true);
                Location location = map.getMyLocation();
                if (location == null) {
                    //Toast.makeText(getBaseContext(), "Please turn on your GPS", Toast.LENGTH_SHORT).show();
                    final AlertDialog.Builder adBuilder = new AlertDialog.Builder(MapsActivity.this);
                    adBuilder.setMessage("Enable GPS to display your current location on to the map?")
                            .setTitle("GPS Not Turned On")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog alert = adBuilder.create();
                    alert.show();
                } else {
                    double distance = estimateDistance(location.getLatitude(), location.getLongitude(), to.coordinate.latitude, to.coordinate.longitude);
                    Log.w("NAVNUS", "Remaining Distance : " + distance);
                    distanceLeftTV.setText(Math.round(distance) + "m left to your destination");
                }

            }
        });

        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this); //This calls onMapRead when it is done

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

    /*
     * Estimates distance between two points in latitude and longitude
     *
     * lat1, lon1 Start point, lat2, lon2 End point
     * @returns Distance in Meters
     */
    public static double estimateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6378137; // Radius of the earth

        Double latDistance = lat2 - lat1;
        Double lonDistance = lon2 - lon1;
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to meters

        return Math.sqrt(distance);
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
    /*
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
    */

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },MY_PERMISSION_ACCESS_FINE_LOCATION );
        }
        if(locationManager!=null)
            locationManager.removeUpdates(listener);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}