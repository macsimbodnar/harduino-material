package com.mazerfaker.harduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mazerfaker.harduino.Constants;
import com.mazerfaker.harduino.MainActivity;
import com.mazerfaker.harduino.R;
import com.mazerfaker.harduino.bluetooth.ConnectedThread;
import com.mazerfaker.harduino.fragments.BaseFragment;
import com.mazerfaker.harduino.fragments.DeviceFragment;
import com.mazerfaker.harduino.fragments.DeviceListContentFragment;
import com.mazerfaker.harduino.fragments.PairedDeviceListContentFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class DeviceActivity extends AppCompatActivity {
    private static final String TAG = "DeviceActivity";

    private ConnectedThread mConnectedThread;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private boolean flag = false;


    // SPP UUID service - this should work for most devices //
    private static final UUID BTMODULEUUID = UUID.fromString(Constants.DEFAULT_UUID);


    private final Handler bluetoothIn = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Constants.MESSAGE_HANDLER_STATE) {
                String readMessage = (String) msg.obj;

                /*****************************
                 *
                 *  DO WITH readMessage WHAT YOU WANT
                 *  for example
                 *  for example Toast message
                 *
                 *****************************/
                Toast.makeText(getBaseContext(), readMessage, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device);
        Button btnSend = (Button) findViewById(R.id.submit_button);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        // Set up onClick listeners //
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(flag) {
                    mConnectedThread.write("~on");
                    flag = false;
                } else {
                    mConnectedThread.write("~off");
                    flag = true;
                }
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

        /*********************************************************************************************
         *
         * send a character when resuming.beginning transmission to check device is connected
         * if it is not an exception will be thrown in the write method and finish() will be called
         *
         *********************************************************************************************/

        mConnectedThread.write("x");
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
