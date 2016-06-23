package com.navnus.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.navnus.R;

import java.util.Timer;
import java.util.TimerTask;

public class SubmitSC extends AppCompatActivity {
    /* GPS Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    private Button recordBtn,stopBtn,submitBtn,cancelBtn;
    private TextView display;
    private LocationManager locationManager;
    private Location currentLocation;
    private Timer myTimer;

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
    }

    public void button_record_click(View view) {

        //Check if user turn on GPS
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }else {//GPS is enabled

            stopBtn.setEnabled(true);
            recordBtn.setEnabled(false);

            //print log on tvDisplay
            display.setText("Testing.....\n");

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
            //Take current coordinate and display on TV
            display.setText(display.getText() + "\nGPS Locations: "+currentLocation.getLatitude()+", "+currentLocation.getLongitude() +"\n");
        }catch(Exception e){
            System.out.println("GPS not turned on.");
            Toast.makeText(getBaseContext(), "GPS not turned on.", Toast.LENGTH_SHORT).show();
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

    public void button_stop_click(View view) {
        myTimer.cancel(); //stops getting loc every 5 secs
        stopBtn.setEnabled(false);
        submitBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);

        //take current coordinate as end point
        getGPSLoc();
    }

    public void button_submit_click(View view) {
        //call email sending function
        Toast.makeText(getBaseContext(), "Email sending function", Toast.LENGTH_SHORT).show();

        //if success
        submitBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setVisibility(View.INVISIBLE);
        recordBtn.setEnabled(true);
        display.setText("");
        //if fail
        //Toast.makeText(getBaseContext(), "An unexpected error had occured.", Toast.LENGTH_SHORT).show();
    }

    public void button_cancel_click(View view) {
        //revert to original state
        recordBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        submitBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setVisibility(View.INVISIBLE);
        display.setText("");
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
}
