package com.example.marei.smartphonetracking;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tuenti.smsradar.Sms;
import com.tuenti.smsradar.SmsListener;
import com.tuenti.smsradar.SmsRadar;

public class TrackerActivity extends Activity {

    //============== BLUETOOTH STUFFS========================

    BluetoothSPP bt;

    Button bluetoothbutton;
    Button senddata;

    String data1 = "";

    Button bluetoothconnect;
    Button bluetoothsend;

    //============== BLUETOOTH STUFFS END =====================

    // Google Map
    private GoogleMap googleMap;

    MarkerOptions marker;
    Marker m;

    Double latitude = 0.0;
    Double longitude = 0.0;

    TextView trackerstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        trackerstatus = (TextView) findViewById(R.id.trackerstatus);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        bt = new BluetoothSPP(this);

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                Log.d("MSG:", message);

                String[] datas_separated = TextUtils.split(message, "#");
                Log.d("INFO:", "LAT : " + datas_separated[0] + "\nLON :" + datas_separated[1]);

                latitude = Double.parseDouble(datas_separated[0]);
                longitude = Double.parseDouble(datas_separated[1]);

                updateUI();



                //bluetoothtext.setText("LAT : " + datas_separated[0] + "\nLON :" + datas_separated[1]);
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });


        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // create marker
        marker = new MarkerOptions().position(new LatLng(0.0, 0.0)).title("Your Location");
        m = googleMap.addMarker(marker);

        SmsRadar.initializeSmsRadarService(getApplicationContext(), new SmsListener() {
            @Override
            public void onSmsSent(Sms sms) {
                showSmsToast(sms);
            }

            @Override
            public void onSmsReceived(Sms sms) {
                showSmsToast(sms);
            }
        });
    }

    private void showSmsToast(Sms sms) {
        Toast.makeText(this, sms.getMsg(), Toast.LENGTH_LONG).show();

        Log.d("SMS RECEIVEDDD!! :", sms.getMsg());

        String[] datas_separated = TextUtils.split(sms.getMsg(), "#");
        Log.d("INFO:", "LAT : " + datas_separated[0] + "\nLON :" + datas_separated[1]);

        latitude = Double.parseDouble(datas_separated[0]);
        longitude = Double.parseDouble(datas_separated[1]);

        trackerstatus.setText("GPS Coordinate : " + latitude + "," + longitude);

        updateUI();

    }

    private void initilizeMap() {

        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map2)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    private void updateUI() {
        Log.d("STATE : ", "UI update initiated .............");

        Log.d("INFO:", "LAT2 : " + latitude + "\nLON2 :" + longitude);

        m.setPosition(new LatLng(latitude, longitude));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(latitude, longitude)).zoom(18).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onStart() {
        super.onStart();

        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                //setup();
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }



}
