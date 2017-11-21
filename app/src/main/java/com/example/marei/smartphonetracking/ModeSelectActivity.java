package com.example.marei.smartphonetracking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ModeSelectActivity extends Activity {

    Spinner modespinner;
    Button enterbutton;
    Button settingbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_select);

        enterbutton = (Button) findViewById(R.id.enterbutton);
        settingbutton = (Button) findViewById(R.id.settingbutton);

        modespinner = (Spinner) findViewById(R.id.modespinner);
        List<String> list = new ArrayList<String>();
        list.add("TRACKER");
        list.add("TARGET");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modespinner.setAdapter(dataAdapter);

        addListenerOnButton();


    }

    public void addListenerOnButton() {

        enterbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Log.d("spinner val : ", String.valueOf(modespinner.getSelectedItem()));

                if(String.valueOf(modespinner.getSelectedItem()).equals("TRACKER")){
                    Log.d("spinner val : ", "1");
                    Intent intent = new Intent(getApplicationContext(),TrackerActivity.class);
                    startActivity(intent);
                } else if(String.valueOf(modespinner.getSelectedItem()).equals("TARGET")){
                    Log.d("spinner val : ", "2");
                    Intent intent = new Intent(getApplicationContext(),TargetActivity.class);
                    startActivity(intent);
                }


            }

        });

        settingbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Log.d("spinner val : ", String.valueOf(modespinner.getSelectedItem()));


                    Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                    startActivity(intent);



            }

        });

    }
}
