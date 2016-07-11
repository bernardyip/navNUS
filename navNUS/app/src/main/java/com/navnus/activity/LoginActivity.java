package com.navnus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.navnus.R;

public class LoginActivity extends AppCompatActivity {
    Button login, register, guestLogin;
    EditText username, password;
    ImageView smiley;
    ProgressDialog dialog;
    String userID;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);

        guestLogin = (Button) findViewById(R.id.guestLogin);
        guestLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                SharedPreferences settings = getSharedPreferences("LoginDetail", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("loginID", "Guest");
                editor.putBoolean("isMember", false);
                // Commit the edits!
                editor.commit();

                Intent intent = new Intent(LoginActivity.this,TempMenu.class);
                startActivity(intent);
                finish();
            }
        });

        register = (Button) findViewById(R.id.button2);
        register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(LoginActivity.this,Registration.class);
                startActivity(intent);
            }
        });

        login = (Button) findViewById(R.id.button1);
        login.setEnabled(false);
        login.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                dialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true, true, new DialogInterface.OnCancelListener(){
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                String uname = username.getText().toString();
                String pwd = password.getText().toString();
                //new LoginTask().execute(uname, pwd);

                //For testing purposes, to remove before official release
                if(uname.equals("admin") && pwd.equals("admin")) {
                    SharedPreferences settings = getSharedPreferences("LoginDetail", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("loginID", uname);
                    editor.putBoolean("isMember", true);
                    // Commit the edits!
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this,TempMenu.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        username.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.length()!=0 && password.getText().toString().length()!=0)
                {
                    login.setEnabled(true);
                }
                else if(s.length()==0 || password.getText().toString().length()==0){
                    login.setEnabled(false);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.length()!=0 && username.getText().toString().length()!=0)
                {
                    login.setEnabled(true);
                }
                else if(s.length()==0 || username.getText().toString().length()==0){
                    login.setEnabled(false);
                }
            }
        });
    }

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