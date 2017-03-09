package com.mazerfaker.harduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.mazerfaker.harduino.bluetooth.ConnectedThread;
import com.mazerfaker.harduino.components.VerticalSeekBar;

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
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        setContentView(R.layout.activity_device);
        final Button forwardButton = (Button) findViewById(R.id.forward_button);
        final Button backButton = (Button) findViewById(R.id.back_button);
        Button startButton = (Button) findViewById(R.id.nos_button);
        Button stopButton = (Button) findViewById(R.id.stop_button);
        Button forwardLeftButton = (Button) findViewById(R.id.forward_left_button);
        Button forwardRightButton = (Button) findViewById(R.id.forward_right_button);
        Button backLeftButton = (Button) findViewById(R.id.back_left_button);
        Button backRightButton = (Button) findViewById(R.id.back_right_button);
        FloatingActionButton fireButton = (FloatingActionButton) findViewById(R.id.fire_button);

        final TextView sliderTextLeft = (TextView)findViewById(R.id.verticalSeekbarTextLeft);
        final TextView sliderTextRight = (TextView)findViewById(R.id.verticalSeekbarTextRight);
        final TextView elevationText = (TextView)findViewById(R.id.gunElevetionText);

        final VerticalSeekBar seekBarLeft = (VerticalSeekBar)findViewById(R.id.seek_bar_left);
        seekBarLeft.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mConnectedThread.write("~" + String.format("%03d", progress) + "l");
                sliderTextLeft.setText("" + progress);
            }
        });

        final VerticalSeekBar seekBarRight = (VerticalSeekBar)findViewById(R.id.seek_bar_right);
        seekBarRight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mConnectedThread.write("~" + String.format("%03d", progress) + "r");
                sliderTextRight.setText("" + progress);
            }
        });

        final VerticalSeekBar elevation = (VerticalSeekBar) findViewById(R.id.gun_elevation);
        elevation.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mConnectedThread.write("~" + String.format("%03d", progress) + "e");
                elevationText.setText("" + progress);
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write("~af");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write("~ab");
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write("~go");
                seekBarRight.setProgress(255);
                seekBarLeft.setProgress(255);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write("~st");
                seekBarRight.setProgress(0);
                seekBarLeft.setProgress(0);
            }
        });

        forwardLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write("~lf");
            }
        });

        forwardRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write("~rf");
            }
        });

        backLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write("~lb");
            }
        });

        backRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write("~rb");
            }
        });

        fireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write("~gf");
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
