package com.navnus.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.navnus.R;
import com.navnus.entity.Map;

import java.text.DecimalFormat;
import java.util.LinkedList;

import edu.princeton.cs.algs4.DirectedEdge;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load map data
        Map.initialize(this);
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
}
