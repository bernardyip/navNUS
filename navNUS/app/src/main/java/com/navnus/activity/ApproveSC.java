package com.navnus.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
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
import com.navnus.datastore.shortcutApi.ShortcutApi;
import com.navnus.datastore.shortcutApi.model.Shortcut;
import com.navnus.entity.GeoCoordinate;
import com.navnus.entity.Map;
import com.navnus.entity.Vertex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;

import edu.princeton.cs.algs4.DirectedEdge;

public class ApproveSC extends AppCompatActivity {
    private Long id;
    private String username, date, coordinates, email;
    private boolean status;
    Button updateBtn, deleteBtn;
    ProgressDialog dialog;
    CheckBox approveCB, disapproveCB;
    private MapboxMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_sc);

        //Get the additional data that was passed from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong("id");
            username = extras.getString("username");
            date = extras.getString("date");
            coordinates = extras.getString("coordinates");
            email = extras.getString("email");
            status = extras.getBoolean("status");
        }
        TextView idTV = (TextView) findViewById(R.id.scIDTV);
        TextView dateTV = (TextView) findViewById(R.id.scDateTV);
        TextView userTV = (TextView) findViewById(R.id.scSubmitByTV);
        TextView coorTV = (TextView) findViewById(R.id.coordinatesTV);
        coorTV.setMovementMethod(new ScrollingMovementMethod());
        approveCB = (CheckBox) findViewById(R.id.approveCB);
        disapproveCB = (CheckBox) findViewById(R.id.disapproveCB);
        idTV.setText("ID : " + id);
        dateTV.setText("Date Submitted : " + date);
        userTV.setText("Submitted By : " + username);
        coorTV.setText(coordinates);
        if(status){
            approveCB.setChecked(true);
        }else{
            disapproveCB.setChecked(true);
        }

        updateBtn = (Button) findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                new UpdateSCTask().execute();

                dialog = ProgressDialog.show(ApproveSC.this, "", "Updating record... Please Wait...", true, true, new DialogInterface.OnCancelListener(){
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
            }
        });

        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Dialog retDialog = new AlertDialog.Builder(ApproveSC.this)
                        .setTitle("Confirm Delete Record?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        new DeleteSCTask().execute();
                                        dialog = ProgressDialog.show(ApproveSC.this, "", "Deleting record... Please Wait...", true, true, new DialogInterface.OnCancelListener(){
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                finish();
                                            }
                                        });
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapView mapView = (MapView)findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                map.isMyLocationEnabled();
                //Get the src and dest
                String data = coordinates;
                ArrayList<String> latList = new ArrayList<String>();
                ArrayList<String> lngList = new ArrayList<String>();
                BufferedReader bufReader = new BufferedReader(new StringReader(data));
                String line=null;
                try {
                    while ((line = bufReader.readLine()) != null) {
                        if(line.contains("GPS Locations:")){
                            latList.add(line.substring(line.indexOf(":")+2,line.indexOf(",")));
                            lngList.add(line.substring(line.indexOf(",")+2));
                            System.out.println("Check:" + latList.get(0) +","+lngList.get(0));
                        }
                    }
                }catch (Exception e){
                    System.out.println("Coordinates data cannot be process properly.");
                }

                // Add markers
                MarkerViewOptions startMarker = new MarkerViewOptions().position(new LatLng(Double.parseDouble(latList.get(0)), Double.parseDouble(lngList.get(0)))).title("Shortcut Start");
                MarkerViewOptions endMarker = new MarkerViewOptions().position(new LatLng(Double.parseDouble(latList.get(latList.size()-1)), Double.parseDouble(lngList.get(lngList.size()-1)))).title("Shortcut End");
                map.addMarker(startMarker);
                map.addMarker(endMarker);

                //Draw the route
                if (latList != null) {
                    PolylineOptions route = new PolylineOptions();
                    double prevLat =0;
                    double prevLng =0;
                    int markerCount = 0;
                    for (int i=0; i<latList.size(); i++) {
                        double currentLat = Double.parseDouble(latList.get(i));
                        double currentLng = Double.parseDouble(lngList.get(i));
                        //removes duplicate coordinates
                        if(prevLat != currentLat || prevLng != currentLng) {
                            route.add(new LatLng(Double.parseDouble(latList.get(i)), Double.parseDouble(lngList.get(i))));
                            if(i>0 && i<latList.size()-1) {
                                //Handles case where the second last pair of coordinates equals the last pair
                                if(latList.size()>2 && i == latList.size()-2){
                                    if(currentLat != Double.parseDouble(latList.get(latList.size()-1)) || currentLng != Double.parseDouble(lngList.get(lngList.size()-1))){
                                        MarkerViewOptions midMarker = new MarkerViewOptions().position(new LatLng(currentLat, currentLng)).title(markerCount + "");
                                        map.addMarker(midMarker);
                                    }
                                }else {
                                    MarkerViewOptions midMarker = new MarkerViewOptions().position(new LatLng(currentLat, currentLng)).title(markerCount + "");
                                    map.addMarker(midMarker);
                                }
                            }
                            prevLat = currentLat;
                            prevLng = currentLng;
                            markerCount++;
                        }
                    }
                    map.addPolyline(route);
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
            }
        });


    }

    public void onCheckboxClicked(View view) {
        switch (view.getId()) {
            case R.id.approveCB:
                disapproveCB.setChecked(false);
                status = true;
                break;

            case R.id.disapproveCB:
                approveCB.setChecked(false);
                status = false;
                break;
        }
    }

    private class UpdateSCTask extends AsyncTask<String, Void, Integer> {
        private ShortcutApi myApiService = null;

        @Override
        protected Integer doInBackground(String... arg0) {
            if(myApiService == null) {  // Only do this once
                ShortcutApi.Builder builder = new ShortcutApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("https://navnus-1370.appspot.com/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();
            }

            Shortcut shortcut = new Shortcut();
            shortcut.setUsername(username);
            shortcut.setEmail(email);
            shortcut.setDate(date);
            shortcut.setCoordinates(coordinates);
            shortcut.setStatus(status);

            try{
                myApiService.updateShortcut(id, shortcut).execute();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return 0;
            }
            return 1;
        }

        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            if (result==1) {
                Toast toast = Toast.makeText(getApplicationContext(), "Shortcut updated successfully.", Toast.LENGTH_LONG);
                toast.show();
            }else {
                Toast toast = Toast.makeText(getApplicationContext(), "An unexpected error had occurred. Please contact administrator if problem persists.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private class DeleteSCTask extends AsyncTask<String, Void, Integer> {
        private ShortcutApi myApiService = null;

        @Override
        protected Integer doInBackground(String... arg0) {
            if(myApiService == null) {  // Only do this once
                ShortcutApi.Builder builder = new ShortcutApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("https://navnus-1370.appspot.com/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();
            }

            try{
                myApiService.removeShortcut(id).execute();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return 0;
            }
            return 1;
        }

        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            if (result==1) {
                Toast toast = Toast.makeText(getApplicationContext(), "Shortcut deleted successfully.", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), AdminSCListDisplay.class);
                startActivity(intent);
                finish();
            }else {
                Toast toast = Toast.makeText(getApplicationContext(), "An unexpected error had occurred. Please contact administrator if problem persists.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    //Overwrite "back" button on android phones
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), AdminSCListDisplay.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
