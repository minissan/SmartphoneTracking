package com.example.marei.smartphonetracking;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingActivity extends Activity {

    String trackerno = "0000000";

    String pinno = "1234";

    TextView trackernumber;

    Button edittrackerno;
    Button editpin;
   // Button addnum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        loadtrackernumber();

        trackernumber = (TextView)findViewById(R.id.trackernumber);

        trackernumber.setText(trackerno);

        edittrackerno = (Button)findViewById(R.id.edittrackerno);
        editpin = (Button)findViewById(R.id.editpin);
        //addnum = (Button)findViewById(R.id.buttonadd);

        trackernumber.setText("TRACKER NO :" + trackerno);

        addListenerOnButton();


    }

    private void addListenerOnButton() {

        edittrackerno.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final EditText new_title = new EditText(SettingActivity.this);
                new_title.setInputType(InputType.TYPE_CLASS_NUMBER);

                // Set the default text to a link of the Queen
                new_title.setHint("Tracker Number");

                new android.app.AlertDialog.Builder(SettingActivity.this)
                        .setTitle("Tracker Number")
                        .setMessage("Enter the number : ")
                        .setView(new_title)
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("trackernumber", String.valueOf(new_title.getText()));
                                editor.commit();

                                trackernumber.setText(new_title.getText());

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Toast.makeText(getApplicationContext(),"Title not changed", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        })
                        .show();


            }

        });

        editpin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final EditText new_pin = new EditText(SettingActivity.this);
                new_pin.setInputType(InputType.TYPE_CLASS_NUMBER);

                // Set the default text to a link of the Queen
                new_pin.setHint("PIN Number");

                new android.app.AlertDialog.Builder(SettingActivity.this)
                        .setTitle("PIN Number")
                        .setMessage("Enter new security PIN : ")
                        .setView(new_pin)
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {



                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Toast.makeText(getApplicationContext(),"Title not changed", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        })
                        .show();

            }

        });






    }

    private void loadtrackernumber(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        trackerno = sharedPreferences.getString("trackernumber", "1234567890");

    }

    private void savetrackernumber(){



    }
}
