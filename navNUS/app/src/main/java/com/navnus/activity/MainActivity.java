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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.navnus.R;
import com.navnus.entity.Map;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.regex.Pattern;

import edu.princeton.cs.algs4.DirectedEdge;

public class MainActivity extends AppCompatActivity {

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

        int fromId = -1;
        int toId = -1;

        //Make sure from and to are id (for now)
        try {
            fromId = Integer.parseInt(from);
            toId = Integer.parseInt(to);

            //Hack to go to navigation
            if (fromId == 1 && toId == 1) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }


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
            } else {
                for (DirectedEdge edge : path) {
                    pathString.append(Map.getVertex(edge.from()).name + " -> " + Map.getVertex(edge.to()).name + "\n");
                }
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

    //Overwrite "back" button on android phones to prompt exit app
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Dialog retDialog = new AlertDialog.Builder(this)
                    .setTitle("Confirm Exit ?")
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    //Exit app
                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                    intent.addCategory(Intent.CATEGORY_HOME);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
