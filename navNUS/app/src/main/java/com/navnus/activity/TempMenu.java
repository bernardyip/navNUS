package com.navnus.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.navnus.R;

public class TempMenu extends AppCompatActivity {
    Button navNus, submitSC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_menu);

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
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SubmitSC.class);
                startActivity(intent);
            }
        });
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
