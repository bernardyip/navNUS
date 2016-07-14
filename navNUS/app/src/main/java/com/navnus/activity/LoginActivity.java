package com.navnus.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.navnus.R;
import com.navnus.datastore.memberApi.MemberApi;
import com.navnus.datastore.memberApi.model.Member;

import java.io.IOException;

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

                new LoginTask().execute(new Pair<Context, String>(LoginActivity.this, uname), new Pair<Context, String>(LoginActivity.this, pwd));

                //For testing purposes, to remove before official release
                /*if(uname.equals("admin") && pwd.equals("admin")) {
                    SharedPreferences settings = getSharedPreferences("LoginDetail", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("loginID", uname);
                    editor.putBoolean("isMember", true);
                    // Commit the edits!
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this,TempMenu.class);
                    startActivity(intent);
                    finish();
                }else{
                    new LoginTask().execute(new Pair<Context, String>(LoginActivity.this, uname), new Pair<Context, String>(LoginActivity.this, pwd));
                }*/
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

    class LoginTask extends AsyncTask<Pair<Context, String>, Void, Boolean > {
        private MemberApi myApiService = null;
        private Context context;
        Member member;

        @Override
        protected Boolean doInBackground(Pair<Context, String>... params) {
            if(myApiService == null) {  // Only do this once
                MemberApi.Builder builder = new MemberApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("https://navnus-1370.appspot.com/_ah/api/")
                        .setApplicationName("navNUS")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();
            }

            context = params[0].first;
            String name = params[0].second;
            String pwd = params[1].second;

            try {
                member = myApiService.getMember(name).execute();
                if(member != null){
                    if(!pwd.equals(member.getPassword()))
                        return false;
                }else
                    return false;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if(result){
                SharedPreferences settings = getSharedPreferences("LoginDetail", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("loginID", username.getText().toString());
                editor.putString("pwd", password.getText().toString());
                editor.putString("email", member.getEmail());
                editor.putBoolean("isAdmin", member.getAdmin());
                editor.putBoolean("isMember", true);
                // Commit the edits!
                editor.commit();
                Intent intent = new Intent(LoginActivity.this,TempMenu.class);
                startActivity(intent);
                finish();
            }else
                Toast.makeText(context, "Incorrect login credentials. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}