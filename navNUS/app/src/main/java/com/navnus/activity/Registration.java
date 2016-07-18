package com.navnus.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.navnus.R;
import com.navnus.datastore.memberApi.MemberApi;
import com.navnus.datastore.memberApi.model.Member;

import java.io.IOException;

public class Registration extends AppCompatActivity {

    EditText name, email, pwd, cfmPwd;
    Button registerBtn;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        pwd = (EditText) findViewById(R.id.pwd);
        cfmPwd = (EditText) findViewById(R.id.cfmPwd);

        registerBtn = (Button) findViewById(R.id.done);
        registerBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                //check that all fields are entered
                if(name.getText().toString().trim().equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter username", Toast.LENGTH_SHORT);
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
                        dialog = ProgressDialog.show(Registration.this, "", "Registering your account...", true, true, new DialogInterface.OnCancelListener(){
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        });
                        new RegistrationTask().execute(email.getText().toString(), pwd.getText().toString(), name.getText().toString());

                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), "Passwords did not match. Please try again.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
    }

    private class RegistrationTask extends AsyncTask<String, Void, Integer> {
        private MemberApi myApiService = null;

        @Override
        protected Integer doInBackground(String... arg0) {
            if(myApiService == null) {  // Only do this once
                MemberApi.Builder builder = new MemberApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("https://navnus-1370.appspot.com/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();
            }
            String email = arg0[0];
            String pwd = arg0[1];
            String name = arg0[2];
            boolean isExist = false;

            //checks if member already exists in database
            try{
                Member member = myApiService.getMember(name).execute();
                if(member!= null)
                    isExist = true;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            //inserts a new member record into database
            try {
                if(isExist){
                        return 2; //username already exist
                }else{
                    Member newMember = new Member();
                    newMember.setUsername(name);
                    newMember.setPassword(pwd);
                    newMember.setEmail(email);
                    newMember.setAdmin(false);
                    myApiService.insertMember(newMember).execute();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return 0; //any other unknown error
            }
            return 1;
        }

        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            if (result==1) {
                Toast toast = Toast.makeText(getApplicationContext(), "Registration successful! Please Login.", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(Registration.this,LoginActivity.class);
                startActivity(intent);
                finish();
            } else if(result==2) {
                Toast toast = Toast.makeText(getApplicationContext(), "Username already exists, please try again.", Toast.LENGTH_LONG);
                toast.show();
            }else {
                Toast toast = Toast.makeText(getApplicationContext(), "Registration failed. Please contact administrator if problem persists.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
