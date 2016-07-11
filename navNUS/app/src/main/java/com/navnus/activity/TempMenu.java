package com.navnus.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.navnus.R;

public class TempMenu extends AppCompatActivity {
    Button navNus, submitSC;
    String userID;
    boolean isMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_menu);

        SharedPreferences settings = getSharedPreferences("LoginDetail", 0);
        userID = settings.getString("loginID", "Guest");
        isMember = settings.getBoolean("isMember", false);

        navNus = (Button) findViewById(R.id.navNusBtn);
        navNus.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        submitSC = (Button) findViewById(R.id.subSCBtn);
        submitSC.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0) {
                if(!isMember){
                    Toast.makeText(getBaseContext(), "Member-only feature. Please login first.", Toast.LENGTH_LONG).show();
                }else {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), SubmitSC.class);
                    startActivity(intent);
                }
            }
        });

        Toast.makeText(getBaseContext(), "Welcome " + userID, Toast.LENGTH_SHORT).show();
        if(!isMember){
            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setMessage("You are currently login as \"Guest\".\n You will have access to limited features only.\n Register an account now to enjoy all the features of this app.")
                    .setCancelable(false)
                    .setPositiveButton("Register Now", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            Intent intent = new Intent(TempMenu.this,Registration.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();
        }
    }

    //Overwrite "back" button on android phones to prompt exit app
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SharedPreferences settings = getSharedPreferences("LoginDetail", 0);
            isMember = settings.getBoolean("isMember", false);
            if(!isMember){
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }else {
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
        }
        return super.onKeyDown(keyCode, event);
    }
}
