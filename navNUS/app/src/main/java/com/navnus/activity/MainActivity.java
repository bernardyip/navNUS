package com.navnus.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.navnus.R;
import com.navnus.entity.Map;
import com.navnus.util.CustomAutoCompleteTextChangedListener;
import com.navnus.util.CustomAutoCompleteView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.regex.Pattern;

import edu.princeton.cs.algs4.DirectedEdge;

public class MainActivity extends AppCompatActivity {

    private int fromId;
    private int toId;
    public String from, to;
    public AutoCompleteTextView  fromAutoComplete, toAutoComplete;

    // adapter for auto-complete
    public ArrayAdapter<String> fromAdapter, toAdapter;
    // just to add some initial value
    public String[] item = new String[] {"Please search..."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            fromAutoComplete = (AutoCompleteTextView) findViewById(R.id.etFrom);
            fromAutoComplete.setThreshold(0);
            toAutoComplete = (AutoCompleteTextView) findViewById(R.id.etTo);
            toAutoComplete.setThreshold(0);

            // add the listener so it will tries to suggest while the user types
            fromAutoComplete.addTextChangedListener(new CustomAutoCompleteTextChangedListener(this,1));
            toAutoComplete.addTextChangedListener(new CustomAutoCompleteTextChangedListener(this,2));

            // set our adapter
            fromAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
            fromAutoComplete.setAdapter(fromAdapter);
            toAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
            toAutoComplete.setAdapter(toAdapter);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //initialize the variables to avoid null pointer
        from = ((AutoCompleteTextView)findViewById(R.id.etFrom)).getText().toString();
        to = ((AutoCompleteTextView)findViewById(R.id.etTo)).getText().toString();
    }

    //Testing button click
    public void button_search_click(View view) {
        TextView debugText = (TextView)findViewById(R.id.textview_debug_text);
        try {
            from = ((AutoCompleteTextView) findViewById(R.id.etFrom)).getText().toString();
            to = ((AutoCompleteTextView) findViewById(R.id.etTo)).getText().toString();
            //((Button)findViewById(R.id.button_view_map)).setVisibility(View.VISIBLE);
            fromAdapter.clear();
            toAdapter.clear();
        }catch(Exception e){
            System.out.println("ERROR :  " + e.getMessage());
            ((Button)findViewById(R.id.button_view_map)).setVisibility(View.INVISIBLE);
        }

        fromId = -1;
        toId = -1;

        //Make sure from and to are id (for now)
        try {
            fromId = Map.getIdFromName(from);
            toId = Map.getIdFromName(to);

            LinkedList<DirectedEdge> path = Map.getPath(fromId, toId);
            StringBuffer pathString = new StringBuffer();
            if (path == null) {
                pathString.append("No path exists!");
            } else if (path.size() > 1) {
                DirectedEdge sourceEdge = path.get(0);
                pathString.append(Map.getVertex(sourceEdge.from()).name + " (" + Map.getVertex(sourceEdge.from()).id + ")\n");
                for (DirectedEdge edge : path) {
                    pathString.append(Map.getVertex(edge.to()).name + " (" + Map.getVertex(edge.to()).id + ")\n");
                }
                //((Button)findViewById(R.id.button_view_map)).setVisibility(View.VISIBLE);
                button_view_map_click(view);
            } else {
                for (DirectedEdge edge : path) {
                    pathString.append(Map.getVertex(edge.from()).name + " -> " + Map.getVertex(edge.to()).name + "\n");
                }
                //((Button)findViewById(R.id.button_view_map)).setVisibility(View.VISIBLE);
                button_view_map_click(view);
            }

            debugText.setText(pathString);
        } catch (Exception e) {
            ((Button)findViewById(R.id.button_view_map)).setVisibility(View.INVISIBLE);
            if(fromId == -1)
                Toast.makeText(MainActivity.this, "Location in \"From\" is invalid. Please try again.", Toast.LENGTH_LONG).show();
            else if(toId == -1)
                Toast.makeText(MainActivity.this, "Location in \"To\" is invalid. Please try again.", Toast.LENGTH_LONG).show();

            debugText.setText("Invalid ID!");
            e.printStackTrace();
        }

        //Hide the keyboard if it is still there
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        //(new ConsolidateVertices()).execute(null, null, null);

    }

    public void button_view_map_click(View view) {
        Intent intent = new Intent();
        intent.putExtra("from", fromId);
        intent.putExtra("to", toId);
        intent.setClass(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    public void setDebugText(String text) {
        TextView debugText = (TextView)findViewById(R.id.textview_debug_text);
        debugText.setText(text);
    }

    //Example for threading long functions (Types are for pre,update,post)
    private class ConsolidateVertices extends AsyncTask<Void, Integer, String> {
        int numberOfDots = 0;
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected String doInBackground(Void... Result) {
            //publishProgress(i);
            return "";
        }

        @Override
        protected void onPreExecute() {
            setDebugText("Loading 00.00%");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            double percentage = values[0]/826.0*100.0;
            DecimalFormat df = new DecimalFormat("##.##");
            setDebugText("Loading " + df.format(percentage) + "%");
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        @Override
        protected void onPostExecute(String result) {
            setDebugText(result);
        }
    }

}
