package com.navnus.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.navnus.R;
import com.navnus.datastore.shortcutApi.ShortcutApi;
import com.navnus.datastore.shortcutApi.model.Shortcut;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminSCListDisplay extends AppCompatActivity {
    List<Shortcut> allSC = null;
    ProgressDialog dialog;
    ArrayAdapter adapter;
    ListView listView;
    Spinner showSpinner, orderBySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sclist_display);

        listView = (ListView) findViewById(R.id.mobile_list);
        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition     = position;
                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);
                // Show Alert
                //Toast.makeText(getApplicationContext(), "Position :"+itemPosition+"  ListItem : " +itemValue.getId() , Toast.LENGTH_LONG).show();
                //Pass the data to the next activity
                Intent intent = new Intent();
                intent.putExtra("id", allSC.get(position).getId());
                intent.putExtra("username", allSC.get(position).getUsername());
                intent.putExtra("date", allSC.get(position).getDate());
                intent.putExtra("coordinates", allSC.get(position).getCoordinates());
                intent.putExtra("email", allSC.get(position).getEmail());
                intent.putExtra("status", allSC.get(position).getStatus());
                intent.setClass(getApplicationContext(), ApproveSC.class);
                startActivity(intent);
                finish();
            }
        });

        dialog = ProgressDialog.show(AdminSCListDisplay.this, "", "Retrieving records... Please wait...", true, true, new DialogInterface.OnCancelListener(){
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        new GetAllSCTask().execute();
    }

    private class GetAllSCTask extends AsyncTask<String, Void, Integer> {
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

            //retrieve all records
            try {
                allSC = myApiService.list().execute().getItems();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return 0; //any other unknown error
            }
            if(allSC == null)
                return 2;
            return 1;
        }

        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            if (result==1) {
                System.out.println("Check size : "+allSC.size());
                if(allSC!=null) {
                    List<Shortcut> temp = allSC;
                    ArrayList<String> processedData = new ArrayList<String>();
                    for(Shortcut s : temp){
                        String processedStr = "Submitted By : "+s.getUsername() + "\nDate : " + s.getDate();
                        if(s.getStatus())
                            processedStr = processedStr + "\nStatus : Approved";
                        else
                            processedStr = processedStr + "\nStatus : Disapproved";

                        processedData.add(processedStr);
                    }
                    //adapter = new ArrayAdapter<Shortcut>(AdminSCListDisplay.this, R.layout.activity_listview, allSC);
                    adapter = new ArrayAdapter<String>(AdminSCListDisplay.this, R.layout.activity_listview, processedData);
                    listView.setAdapter(adapter);
                    Toast toast = Toast.makeText(getApplicationContext(), "Records retrieved successfully.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else if(result==2) {
                Toast toast = Toast.makeText(getApplicationContext(), "No records found.", Toast.LENGTH_SHORT);
                toast.show();
            }else {
                Toast toast = Toast.makeText(getApplicationContext(), "An unexpected error had occurred while retrieving the records.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
