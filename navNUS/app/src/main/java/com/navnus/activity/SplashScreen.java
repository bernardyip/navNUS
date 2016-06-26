package com.navnus.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import com.navnus.R;
import com.navnus.entity.Map;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    private Activity thisActivity;
    private Random random;
    private int[] loadingTexts = {
            R.string.splash_screen_activity_loading_text_1,
            R.string.splash_screen_activity_loading_text_2,
            R.string.splash_screen_activity_loading_text_3,
            R.string.splash_screen_activity_loading_text_4,
            R.string.splash_screen_activity_loading_text_5};
    private TextView loadingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        thisActivity = this;
        loadingTextView = ((TextView)findViewById(R.id.splash_screen_textView_loading_text));
        (new DataLoader()).execute(this, null, null);

        //Loading data text
        random = new Random();
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerMethod();
            }

        }, 0, 5000); //3 secs interval
    }

    private void timerMethod() {
        this.runOnUiThread(randomLoadingText);
    }

    private Runnable randomLoadingText = new Runnable() {
        public void run() {
            int id = random.nextInt(loadingTexts.length);
            loadingTextView.setText(loadingTexts[id]);
        }
    };

    private class DataLoader extends AsyncTask<Context, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(thisActivity);

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
