package com.example.marei.smartphonetracking;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

public class TargetActivity extends Activity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    String trackerno = "0123456789";

    TextView trackernumber;

    String latitude = "";
    String longitude = "";

    long timestamp = 0;
    long sms_timestamp = 0;
    boolean smsflag = false;  // SMS mode is OFF in beginning.


//============== BLUETOOTH STUFFS========================

    BluetoothSPP bt;

    Button bluetoothbutton;
    Button senddata;

    String data1 = "";

    Button bluetoothconnect;
    Button bluetoothsend;

    //============== BLUETOOTH STUFFS END =====================

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    Button btnFusedLocation;
    TextView tvLocation;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;



    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            //finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setContentView(R.layout.activity_target);

        tvLocation = (TextView) findViewById(R.id.tvLocation2);

        trackernumber = (TextView) findViewById(R.id.trackernumber2);

        loadtrackernumber();


        btnFusedLocation.setOnClickListener(new View.OnClickListener() { // no need. only for testing
            @Override
            public void onClick(View arg0) {
                updateUI();
            }
        });

        bluetoothconnect = (Button) findViewById(R.id.bluetoothconnect);


        bluetoothsend = (Button) findViewById(R.id.bluetoothsend);  //no need. only for testing
        bluetoothsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //simpleBluetooth.sendData("SHOTS FIRED!");
            }
        });

        bt = new BluetoothSPP(this);

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                /*Log.d("MSG:", message);

                String[] datas_separated = TextUtils.split(message, "#");

                Double latitude = Double.parseDouble(datas_separated[0]);
                Double longitude = Double.parseDouble(datas_separated[0]);

                Log.d("INFO:", "LAT : " + latitude + "\nLON :" + longitude);*/

                //bluetoothtext.setText("LAT : " + datas_separated[0] + "\nLON :" + datas_separated[1]);
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();

                smsflag = false;
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();

                smsflag = true;
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        addListenerOnButton();



    }

    public void addListenerOnButton() {

        bluetoothconnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //simpleBluetooth.scan(SCAN_REQUEST);
                bt.startService(BluetoothState.DEVICE_OTHER);

                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

                //bt.send("Text", true);

            }

        });

        bluetoothsend.setOnClickListener(new View.OnClickListener() {   //testing only...delete afterword

            @Override
            public void onClick(View arg0) {

                //bt.send("130.12345678#123.12345678", true);

            }

        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                //setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();

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
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();   // UPDATES THE LATITUDE AND LONGITUDE VALUES ON SCREEN LIVE

        timestamp = System.currentTimeMillis();

        if((timestamp - sms_timestamp) > 300000 && smsflag){

            sms_timestamp = System.currentTimeMillis();

            send_sms();

            Toast.makeText(getApplicationContext()
                    , "SENDING GPS DATA VIA SMS!!"
                    , Toast.LENGTH_SHORT).show();

        } else {

            Log.d("SMS STATE", "Not yet time!");

        }
    }

    private void send_sms() {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(trackerno, null, latitude+"#"+longitude, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());

            latitude = lat;
            longitude = lng;

            tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
                    "Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                    "Provider: " + mCurrentLocation.getProvider());

            bt.send(lat+"#"+lng, true);    // bluetooth funtion to SEND the cooridnate to tracker...



        } else {
            Log.d(TAG, "location is null ...............");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }



    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
        //initilizeMap();



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    private void loadtrackernumber(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        trackerno = sharedPreferences.getString("trackernumber", "1234567890");

        trackernumber.setText("TRACKER NO :" + trackerno);

    }
}
