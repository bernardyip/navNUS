package com.navnus.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.navnus.util.Mail;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class SubmitSC extends AppCompatActivity {
    /* GPS Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    private Button recordBtn,stopBtn,submitBtn,cancelBtn;
    private ImageButton helpBtn;
    private TextView display;
    private LocationManager locationManager;
    private Location currentLocation;
    private Timer myTimer;
    ProgressDialog dialog;

    private MapboxMap map;
    private MapView mapView;
    PolylineOptions route;
    int padding;
    int markerCounter=0;
    double prevLat =0;
    double prevLng =0;
    boolean isLast=false;
    MarkerViewOptions startMarker = null;
    MarkerViewOptions secLastKnwnMarker = null;
    MarkerViewOptions lastKnownMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_sc);

        recordBtn = (Button)findViewById(R.id.btn_record);
        stopBtn = (Button)findViewById(R.id.btn_stop);
        submitBtn = (Button)findViewById(R.id.btn_submit);
        cancelBtn = (Button)findViewById(R.id.btn_cancel);
        display = (TextView)findViewById(R.id.tvDisplay);
        display.setMovementMethod(new ScrollingMovementMethod());
        helpBtn = (ImageButton) findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(SubmitSC.this);
                builder.setMessage("RECORD - Click on it to start recording your GPS position every 5secs to form your shortcut.\n\n" +
                        "STOP - Click on it when you've reached the end point of your shortcut.\n\n" +
                        "SUBMIT - This button will only appear after clicking on STOP button, clicking this will submit the shortcut that you've just created.\n\n" +
                        "CANCEL - This button will only appear after clicking on STOP button. Click on this button if you have made a mistake and would like to start over again. All progress will be lost.\n\n" +
                        "*After submitting your shortcut, the Administrator will need to verify the shortcut before approving it for other users to use.")
                        .setTitle("Tutorial - How to Use")
                        .setCancelable(false)
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }

        });
        //If user doesn't have internet access
        if(!isNetworkConnected()){
            buildAlertMessageNoInternet();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

    }

    public void button_record_click(View view) {
        route = new PolylineOptions();
        //Check if user turn on GPS
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }else {//GPS is enabled

            stopBtn.setEnabled(true);
            recordBtn.setEnabled(false);

            //print log on tvDisplay
            display.setText("");
            //getGPSLoc(); //start point

            //Set timer, every 5 seconds capture user's coordinates and display on TV
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    TimerMethod();
                }

            }, 0, 5000); //5 secs interval
        }
    }

    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }


    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            //This method runs in the same thread as the UI.
            //Do something to the UI thread here
            getGPSLoc();
        }
    };

    private void getGPSLoc(){
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },MY_PERMISSION_ACCESS_COARSE_LOCATION );
        }
        //Attempt to get GPS location
        try{
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            String provider = LocationManager.NETWORK_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider,
                    5000, // 5sec
                    1,   // 1m
                    locationListener);
            System.out.println("GPS Locations: "+currentLocation.getLatitude()+", "+currentLocation.getLongitude());

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    map = mapboxMap;
                    map.isMyLocationEnabled();

                    // Add start marker
                    if(markerCounter == 0) {
                        startMarker = new MarkerViewOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Shortcut Start");
                        map.addMarker(startMarker);
                        route.add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                        //Take current coordinate and display on TV
                        display.setText(display.getText() + "\nGPS Locations: "+currentLocation.getLatitude()+", "+currentLocation.getLongitude() +"\n");
                        markerCounter++;
                        prevLat = currentLocation.getLatitude();
                        prevLng = currentLocation.getLongitude();
                    }else {
                        //removes duplicate coordinates
                        if(prevLat != currentLocation.getLatitude() || prevLng != currentLocation.getLongitude()) {
                            route.add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            //Handles case where the second last pair of coordinates equals the last pair
                          /*  if(isLast){
                                if(currentLocation.getLatitude() != prevLat || currentLocation.getLongitude() != prevLng){
                                    lastKnownMarker = new MarkerViewOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Shortcut End");
                                    map.addMarker(lastKnownMarker);
                                    //Take current coordinate and display on TV
                                    display.setText(display.getText() + "\nGPS Locations: "+currentLocation.getLatitude()+", "+currentLocation.getLongitude() +"\n");
                                    markerCounter++;
                                    prevLat = currentLocation.getLatitude();
                                    prevLng = currentLocation.getLongitude();
                                }
                            }else {*/
                                if(markerCounter==1){
                                    secLastKnwnMarker = startMarker;
                                }else{
                                    secLastKnwnMarker = lastKnownMarker;
                                }
                                lastKnownMarker = new MarkerViewOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title(markerCounter + "");
                                map.addMarker(lastKnownMarker);
                                //Take current coordinate and display on TV
                                display.setText(display.getText() + "\nGPS Locations: "+currentLocation.getLatitude()+", "+currentLocation.getLongitude() +"\n");
                                markerCounter++;
                                prevLat = currentLocation.getLatitude();
                                prevLng = currentLocation.getLongitude();
                            //}

                        }
                    }
                    //draws the line from the points
                    map.addPolyline(route);

                    try {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        //Center camera to show latest 2 markers
                        if(markerCounter==1)
                            builder.include(startMarker.getPosition());
                        else
                            builder.include(secLastKnwnMarker.getPosition());

                        builder.include(lastKnownMarker.getPosition());
                        LatLngBounds bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        map.moveCamera(cu);
                        map.setMaxZoom(map.getMaxZoom());
                    }catch(Exception e){
                        System.out.println("ERROR at LatLngBuilder : " + e.getMessage());
                    }
                }
            });

        }catch(Exception e){
            System.out.println("GPS not ready yet. Could be not turned on.");
            Toast.makeText(getBaseContext(), "Searching for GPS signal...", Toast.LENGTH_SHORT).show();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
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
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageNoInternet() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You are not connected to the internet, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    public void button_stop_click(View view) {
        myTimer.cancel(); //stops getting loc every 5 secs
        stopBtn.setEnabled(false);
        submitBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        isLast = true;
        //take current coordinate as end point
        getGPSLoc();
    }

    public void button_submit_click(View view) {
        String coordinates = display.getText().toString();
        Date date = new Date();
        SharedPreferences settings = getSharedPreferences("LoginDetail", 0);
        String userID = settings.getString("loginID", " ");
        String email = settings.getString("email", " ");

        new SubmitSCTask().execute(userID, email, date.toString(), coordinates);

        dialog = ProgressDialog.show(SubmitSC.this, "", "Sending data... Please Wait...", true, true, new DialogInterface.OnCancelListener(){
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        /*  EMAIL CODE
        final String emailBody = display.getText().toString();
        //call email sending function
        dialog = ProgressDialog.show(SubmitSC.this, "", "Sending data... Please Wait...", true, true, new DialogInterface.OnCancelListener(){
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        final Mail m = new Mail("navnus2016@gmail.com", "navNUS123");
        new AsyncTask<Void, Void, Boolean>() {
            @Override public Boolean doInBackground(Void... arg) {
                Boolean success = false;
                try {
                    String[] toArr = {"navnus2016@gmail.com"};
                    m.setTo(toArr);
                    m.setFrom("user@navNUS.com");
                    m.setSubject("New Shortcut Submitted");
                    m.setBody(emailBody); //email body

                    try {
                        //m.addAttachment("/sdcard/filelocation");

                        if(m.send()) {
                            success = true;
                        }
                    } catch(Exception e) {
                        //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                        Log.e("MailApp", "Could not send email", e);
                    }
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
                return success;
            }
            protected void onPostExecute(Boolean result) {
                dialog.dismiss();
                if(result){
                    submitBtn.setVisibility(View.INVISIBLE);
                    cancelBtn.setVisibility(View.INVISIBLE);
                    recordBtn.setEnabled(true);
                    display.setText("");
                    Toast.makeText(SubmitSC.this, "Shortcut uploaded successfully. Pending admin approval.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(SubmitSC.this, "An unexpected error had occurred, please try again", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
        */
    }

    public void button_cancel_click(View view) {
        //revert to original state
        recordBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        submitBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setVisibility(View.INVISIBLE);
        display.setText("");
        markerCounter=0;
        prevLat =0;
        prevLng =0;
        isLast=false;
        map.clear();
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider){}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private void updateWithNewLocation(Location location) {
        // Update your current location
        currentLocation = location;
    }

    @Override
    protected void onDestroy() {
        Log.v("GetDirection", "onDestory");
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },MY_PERMISSION_ACCESS_COARSE_LOCATION );
        }
        if(locationManager!=null)
            locationManager.removeUpdates(locationListener);
        //unregisterReceiver(receiver);
        super.onDestroy();
    }

    //check if you are connected to a network
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private class SubmitSCTask extends AsyncTask<String, Void, Integer> {
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
            shortcut.setUsername(arg0[0]);
            shortcut.setEmail(arg0[1]);
            shortcut.setDate(arg0[2]);
            shortcut.setCoordinates(arg0[3]);
            shortcut.setStatus(false);

            try{
                myApiService.insertShortcut(shortcut).execute();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return 0;
            }
            return 1;
        }

        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            if (result==1) {
                submitBtn.setVisibility(View.INVISIBLE);
                cancelBtn.setVisibility(View.INVISIBLE);
                recordBtn.setEnabled(true);
                display.setText("");
                markerCounter=0;
                prevLat =0;
                prevLng =0;
                isLast=false;
                map.clear();
                Toast toast = Toast.makeText(getApplicationContext(), "Shortcut uploaded successfully. Pending admin approval.", Toast.LENGTH_LONG);
                toast.show();
            }else {
                Toast toast = Toast.makeText(getApplicationContext(), "An unexpected error had occurred. Please contact administrator if problem persists.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}