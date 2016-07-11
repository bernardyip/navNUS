package com.navnus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.navnus.R;

public class Registration extends AppCompatActivity {

    EditText name, email, pwd, cfmPwd;
    Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        pwd = (EditText) findViewById(R.id.pwd);
        cfmPwd = (EditText) findViewById(R.id.cfmPwd);

        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                //check that all fields are entered
                if(name.getText().toString().trim().equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_SHORT);
                    toast.show();
                }else if(email.getText().toString().trim().equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT);
                    toast.show();
                }else if(pwd.getText().toString().trim().equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT);
                    toast.show();
                }else if(cfmPwd.getText().toString().trim().equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please confirm password", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    //Check if cfmPwd and Pwd matches
                    if(pwd.getText().toString().equals(cfmPwd.getText().toString())){
                        //do the registration process here
                        //new RegistrationTask().execute(email.getText().toString(), pwd.getText().toString(), name.getText().toString());

                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), "Passwords did not match. Please try again.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
    }
}
