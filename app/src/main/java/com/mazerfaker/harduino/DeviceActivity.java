package com.mazerfaker.harduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.mazerfaker.harduino.bluetooth.ConnectedThread;
import java.io.IOException;
import java.util.UUID;


public class DeviceActivity extends AppCompatActivity {
    private static final String TAG = "DeviceActivity";

    private ConnectedThread mConnectedThread;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;

    // SPP UUID service - this should work for most devices //
    private static final UUID BTMODULEUUID = UUID.fromString(Constants.DEFAULT_UUID);


    private final Handler bluetoothIn = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == Constants.MESSAGE_HANDLER_STATE) {
                String readMessage = (String) msg.obj;

                /*
                 *
                 *  DO WITH readMessage WHAT YOU WANT
                 *  for example
                 *  for example Toast message
                 *
                 */
                Toast.makeText(getBaseContext(), readMessage, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        setContentView(R.layout.activity_device);
        Button forwardLeftButton = (Button) findViewById(R.id.forward_left_button);
        Button forwardRightButton = (Button) findViewById(R.id.forward_right_button);
        Button backLeftButton = (Button) findViewById(R.id.back_left_button);
        Button backRightButton = (Button) findViewById(R.id.back_right_button);
        Button fireButton = (Button) findViewById(R.id.fire_button);
        Button turretLeftButton = (Button) findViewById(R.id.turret_left_button);
        Button turretRightButton = (Button) findViewById(R.id.turret_right_button);
        Button gunUpButton = (Button) findViewById(R.id.gun_up_button);
        Button gunDownButton = (Button) findViewById(R.id.gun_down_button);

        forwardLeftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mConnectedThread.write(Constants.LEFT_FRONT);
                }

                if(event.getAction() == MotionEvent.ACTION_UP){
                    mConnectedThread.write(Constants.LEFT_STOP);
                }
                return true;
            }
        });

        forwardRightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mConnectedThread.write(Constants.RIGHT_FRONT);
                }

                if(event.getAction() == MotionEvent.ACTION_UP){
                    mConnectedThread.write(Constants.RIGHT_STOP);
                }
                return true;
            }
        });

        backLeftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mConnectedThread.write(Constants.LEFT_BACK);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    mConnectedThread.write(Constants.LEFT_STOP);
                }
                return true;
            }
        });

        backRightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mConnectedThread.write(Constants.RIGHT_BACK);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    mConnectedThread.write(Constants.RIGHT_STOP);
                }
                return true;
            }
        });

        fireButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mConnectedThread.write(Constants.GUN_FIRE);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    mConnectedThread.write(Constants.GUN_FIRE_STOP);
                }
                return true;
            }
        });

        turretLeftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mConnectedThread.write(Constants.TURRET_LEFT);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    mConnectedThread.write(Constants.TURRET_LEFT_STOP);
                }
                return true;
            }
        });

        turretRightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mConnectedThread.write(Constants.RIGHT_TURRET);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    mConnectedThread.write(Constants.RIGHT_TURRET_STOP);
                }
                return true;
            }
        });

        gunUpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mConnectedThread.write(Constants.GUN_UP);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    mConnectedThread.write(Constants.GUN_UP_STOP);
                }
                return true;
            }
        });

        gunDownButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mConnectedThread.write(Constants.GUN_DOWN);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    mConnectedThread.write(Constants.GUN_DOWN_STOP);
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get MAC address from DeviceListActivity via intent //
        Intent intent = getIntent();

        // Get the MAC address from the DeviceListActivty via EXTRA that we have set on DeviceListActivity //
        String address = intent.getStringExtra(Constants.EXTRA_DEVICE_ADDRESS);

        // create device and set the MAC address //
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage());
        }

        // Establish the Bluetooth socket connection //
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            Log.e(TAG, "Try to close connection...");
            try
            {
                btSocket.close();
            } catch (IOException e2) {
                Log.e(TAG, "Failed to connect");
                Log.e(TAG, e.getMessage());
            }
        }

        mConnectedThread = new ConnectedThread(btSocket, bluetoothIn);
        mConnectedThread.start();

        /*
         *
         * send a character when resuming.beginning transmission to check device is connected
         * if it is not an exception will be thrown in the write method and finish() will be called
         *
         */

         //mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            // Don't leave Bluetooth sockets open when leaving activity //
            btSocket.close();
        } catch (IOException e2) {
            Log.e(TAG, e2.getMessage());
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
}
