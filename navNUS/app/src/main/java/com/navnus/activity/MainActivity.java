package com.navnus.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.navnus.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Testing button click
    public void button_search_click(View view) {
        //Testing debugging code
        TextView debugText = (TextView)findViewById(R.id.textview_debug_text);
        String input = ((EditText)findViewById(R.id.edittext_search)).getText().toString();
        debugText.setText(input);
    }
}
