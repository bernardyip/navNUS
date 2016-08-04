/*
    Created By : Team Prop (Orbital 2016)
 */
package com.navnus.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.navnus.R;
import com.navnus.datastore.memberApi.MemberApi;
import com.navnus.datastore.memberApi.model.Member;

import java.io.IOException;
import java.security.MessageDigest;

public class ChangePwd extends AppCompatActivity {
    SharedPreferences settings;
    String userID, email, originalPwd, oldPwd, newPwd, cfmPwd;
    boolean isAdmin;
    EditText pwdTextField;
    TextView text;
    Button next1, next2, done;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);

        settings = getSharedPreferences("LoginDetail", 0);
        userID = settings.getString("loginID", "");
        originalPwd = settings.getString("pwd", "");
        email = settings.getString("email", "");
        isAdmin = settings.getBoolean("isAdmin", false);

        done = (Button) findViewById(R.id.doneButton);
        done.setVisibility(View.GONE);
        next1 = (Button) findViewById(R.id.nextButton1);
        next2 = (Button) findViewById(R.id.nextButton2);
        next2.setVisibility(View.GONE);
        next1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                pwdTextField = (EditText) findViewById(R.id.oldPwd);
                oldPwd = pwdTextField.getText().toString();

                //hash the oldPwd
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    md.update(oldPwd.getBytes());
                    byte byteData[] = md.digest();

                    //convert the byte to hex format
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < byteData.length; i++) {
                        sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                    }

                    oldPwd = sb.toString();
                }catch (Exception e){
                    System.out.println("Error at pwd hashing : "+ e.getMessage());
                }

                //password matched
                if(oldPwd.equals(originalPwd)){
                    text = (TextView) findViewById(R.id.cpHead);
                    text.setText("Enter your new password");
                    pwdTextField.setText("");
                    next1.setVisibility(View.GONE);
                    next2.setVisibility(View.VISIBLE);
                    next2.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View arg0) {
                            newPwd = pwdTextField.getText().toString();
                            System.out.println("NewPwd: "+newPwd);
                            text.setText("Confirm your new password");
                            pwdTextField.setText("");
                            next2.setVisibility(View.GONE);
                            done.setVisibility(View.VISIBLE);
                            done.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View arg0) {
                                    //call the chng pwd method here and check if the newPwd and cfmPwd matches
                                    cfmPwd = pwdTextField.getText().toString();
                                    if(newPwd.equals(cfmPwd)){
                                        //if newPwd and cfmPwd match
                                        //Change the pwd
                                        System.out.println("CfmPwd: "+cfmPwd);
                                        new ChangePwdTask().execute(new Pair<Context, String>(ChangePwd.this, userID), new Pair<Context, String>(ChangePwd.this, cfmPwd));
                                        //start
                                        dialog = ProgressDialog.show(ChangePwd.this, "", "Changing your password...", true, true, new DialogInterface.OnCancelListener(){
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                finish();
                                            }
                                        });
                                    }
                                    else{
                                        //newPwd and cfmPwd mismatched
                                        done.setVisibility(View.GONE);
                                        next2.setVisibility(View.VISIBLE);
                                        text.setText("Enter your new password");
                                        pwdTextField.setText("");
                                        Toast toast = Toast.makeText(getApplicationContext(), "Passwords did not match. Please try again.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }
                            });
                        }
                    });

                }else{
                    //password mismatched
                    Toast toast = Toast.makeText(getApplicationContext(), "Password is incorrect. Please try again.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    class ChangePwdTask extends AsyncTask<Pair<Context, String>, Void, Boolean > {
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

            member = new Member();
            member.setUsername(userID);
            member.setAdmin(isAdmin);
            member.setEmail(email);

            //hash the pwd
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(cfmPwd.getBytes());
                byte byteData[] = md.digest();

                //convert the byte to hex format
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < byteData.length; i++) {
                    sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                }

                cfmPwd = sb.toString();
            }catch (Exception e){
                System.out.println("Error at pwd hashing : "+ e.getMessage());
                return false;
            }

            member.setPassword(cfmPwd);

            try {
                myApiService.updateMember(userID, member).execute();
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
                editor.putString("pwd", cfmPwd);
                // Commit the edits!
                editor.commit();
                Toast.makeText(context, "Password changed successfully.", Toast.LENGTH_SHORT).show();
                finish();
            }else
                Toast.makeText(context, "An unexpected error had occurred. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
