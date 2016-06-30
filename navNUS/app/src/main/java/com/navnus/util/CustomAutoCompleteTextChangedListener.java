package com.navnus.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.navnus.activity.MainActivity;
import com.navnus.entity.Map;

import java.util.ArrayList;

public class CustomAutoCompleteTextChangedListener implements TextWatcher{

    public static final String TAG = "CustomAutoCompleteTextChangedListener.java";
    Context context;
    int who; //1 = from , 2 = to

    public CustomAutoCompleteTextChangedListener(Context context, int who){
        this.context = context;
        this.who = who;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        // if you want to see in the logcat what the user types
        //Log.e(TAG, "User input: " + userInput);

        MainActivity mainActivity = ((MainActivity) context);
        String query= "";
        if(who == 1)
            query = mainActivity.from;
        else if(who == 2)
            query = mainActivity.to;

        ArrayList<String> ddlSuggestions = Map.getSimilarNamesFromName(query);

        // update the adapater
        if(who == 1) {
            mainActivity.fromAdapter.notifyDataSetChanged();
            mainActivity.fromAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_dropdown_item_1line, ddlSuggestions);
            mainActivity.fromAutoComplete.setAdapter(mainActivity.fromAdapter);
        } else if(who == 2) {
            mainActivity.toAdapter.notifyDataSetChanged();
            mainActivity.toAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_dropdown_item_1line, ddlSuggestions);
            mainActivity.toAutoComplete.setAdapter(mainActivity.toAdapter);
        }
    }

}