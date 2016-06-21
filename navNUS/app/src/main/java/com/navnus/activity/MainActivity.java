package com.navnus.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.navnus.R;
import com.navnus.entity.Map;

import java.text.DecimalFormat;
import java.util.Calendar;

import edu.princeton.cs.algs4.DirectedEdge;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load map data
        Calendar c = Calendar.getInstance();
        long seconds = c.get(Calendar.MILLISECOND);
        Map.initialize(this);
        Toast.makeText(this, Long.toString(c.get(Calendar.MILLISECOND) - seconds), Toast.LENGTH_LONG).show();
        System.out.println(Long.toString(c.get(Calendar.MILLISECOND) - seconds));
    }

    //Testing button click
    public void button_search_click(View view) {

        //Testing debugging code
        TextView debugText = (TextView)findViewById(R.id.textview_debug_text);
        //String input = ((EditText)findViewById(R.id.edittext_search)).getText().toString();
        Iterable<DirectedEdge> path = Map.graph.path(93, 97);
        StringBuffer pathString = new StringBuffer();
        for (DirectedEdge edge : path) {
            pathString.append(edge.from() + "->" + edge.to() + "\n");
        }
        debugText.setText(pathString);


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
