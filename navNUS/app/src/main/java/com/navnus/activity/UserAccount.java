/*
    Created By : Team Prop (Orbital 2016)
 */
package com.navnus.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.navnus.R;

public class UserAccount extends AppCompatActivity {
    Button chngPwdBtn,logoutBtn;
    TextView usernameTV;
    boolean isMember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        chngPwdBtn = (Button) findViewById(R.id.chngPwd);

        SharedPreferences settings = getSharedPreferences("LoginDetail", 0);
        isMember = settings.getBoolean("isMember", false);
        if(!isMember){
            chngPwdBtn.setEnabled(false);
            logoutBtn.setText("Login");
        }

        usernameTV = (TextView) findViewById(R.id.usernameTV);
        usernameTV.setText(settings.getString("loginID", ""));

        chngPwdBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(UserAccount.this,ChangePwd.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                SharedPreferences settings = getSharedPreferences("LoginDetail", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("loginID", "");
                editor.putString("pwd", "");
                editor.putString("email", "");
                editor.putBoolean("isAdmin", false);
                editor.putBoolean("isMember", false);
                editor.commit();

                Intent intent = new Intent(UserAccount.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
