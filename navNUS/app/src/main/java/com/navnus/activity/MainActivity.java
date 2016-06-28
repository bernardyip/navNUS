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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.navnus.R;
import com.navnus.entity.Map;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.regex.Pattern;

import edu.princeton.cs.algs4.DirectedEdge;

public class MainActivity extends AppCompatActivity {

    private int fromId;
    private int toId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Testing button click
    public void button_search_click(View view) {
        TextView debugText = (TextView)findViewById(R.id.textview_debug_text);
        String from = ((EditText)findViewById(R.id.etFrom)).getText().toString();
        String to = ((EditText)findViewById(R.id.etTo)).getText().toString();
        ((Button)findViewById(R.id.button_view_map)).setEnabled(false);

        fromId = -1;
        toId = -1;

        ArrayList<String> test = Map.getSimilarNamesFromName("COM");
        Toast.makeText(MainActivity.this, "Test: " + test.toString(), Toast.LENGTH_LONG).show();

        //Make sure from and to are id (for now)
        try {
            fromId = Integer.parseInt(from);
            toId = Integer.parseInt(to);

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
                ((Button)findViewById(R.id.button_view_map)).setEnabled(true);
            } else {
                for (DirectedEdge edge : path) {
                    pathString.append(Map.getVertex(edge.from()).name + " -> " + Map.getVertex(edge.to()).name + "\n");
                }
                ((Button)findViewById(R.id.button_view_map)).setEnabled(true);
            }

            debugText.setText(pathString);
        } catch (Exception e) {
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
