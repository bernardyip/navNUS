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
import com.navnus.R;
import com.navnus.datastore.shortcutApi.ShortcutApi;
import com.navnus.datastore.shortcutApi.model.Shortcut;

import java.io.IOException;

public class ApproveSC extends AppCompatActivity {
    private Long id;
    private String username, date, coordinates, email;
    private boolean status;
    Button updateBtn, deleteBtn;
    ProgressDialog dialog;
    CheckBox approveCB, disapproveCB;

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
