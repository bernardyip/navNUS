package com.navnus.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import com.navnus.R;
import com.navnus.entity.Map;

import java.text.DecimalFormat;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        (new DataLoader()).execute(this, null, null);
    }

    private class DataLoader extends AsyncTask<Context, Void, Void> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected Void doInBackground(Context... context) {
            Map.initialize(context[0]);
            //publishProgress(i);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... arg) {

        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), TempMenu.class);
            startActivity(intent);
            finish();
        }
    }
}
