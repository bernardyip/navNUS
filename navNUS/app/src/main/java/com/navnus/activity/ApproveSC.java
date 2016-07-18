package com.navnus.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.navnus.R;

public class ApproveSC extends AppCompatActivity {
    private Long id;
    private String username, date, coordinates, email;
    private boolean status;
    Button updateBtn, deleteBtn;
    ProgressDialog dialog;
    CheckBox approveCB, disapproveCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_sc);

        //Get the additional data that was passed from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong("id");
            username = extras.getString("username");
            date = extras.getString("date");
            coordinates = extras.getString("coordinates");
            email = extras.getString("email");
            status = extras.getBoolean("status");
        }
        TextView idTV = (TextView) findViewById(R.id.scIDTV);
        TextView dateTV = (TextView) findViewById(R.id.scDateTV);
        TextView userTV = (TextView) findViewById(R.id.scSubmitByTV);
        TextView coorTV = (TextView) findViewById(R.id.coordinatesTV);
        coorTV.setMovementMethod(new ScrollingMovementMethod());
        approveCB = (CheckBox) findViewById(R.id.approveCB);
        disapproveCB = (CheckBox) findViewById(R.id.disapproveCB);
        idTV.setText("ID : " + id);
        dateTV.setText("Date Submitted : " + date);
        userTV.setText("Submitted By : " + username);
        coorTV.setText(coordinates);
        if(status){
            approveCB.setChecked(true);
        }else{
            disapproveCB.setChecked(true);
        }

        updateBtn = (Button) findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //check that all fields are entered
                Toast toast = Toast.makeText(getApplicationContext(), "UpdateClicked.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //check that all fields are entered
                Toast toast = Toast.makeText(getApplicationContext(), "DeleteClicked.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void onCheckboxClicked(View view) {
        switch (view.getId()) {
            case R.id.approveCB:
                disapproveCB.setChecked(false);
                break;

            case R.id.disapproveCB:
                approveCB.setChecked(false);
                break;
        }
    }
}
