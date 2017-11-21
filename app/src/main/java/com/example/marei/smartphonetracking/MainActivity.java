package com.example.marei.smartphonetracking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    Button enterbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterbutton = (Button) findViewById(R.id.enterbutton);

        addListenerOnButton();

    }

    public void addListenerOnButton() {

        enterbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Log.d("STATUS : "," CLICKED ENTER");
                Intent intent = new Intent(getApplicationContext(),ModeSelectActivity.class);
                startActivity(intent);

            }

        });

    }
}
