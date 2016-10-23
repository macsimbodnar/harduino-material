package com.mazerfaker.harduino.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mazerfaker.harduino.Constants;
import com.mazerfaker.harduino.DeviceActivity;
import com.mazerfaker.harduino.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PairedDeviceListContentFragment extends BaseFragment {
    private static final String TAG = "PairedDeviceList...";
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        context = recyclerView.getContext();
        ContentAdapter adapter = new ContentAdapter(context);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avator;
        public TextView name;
        public TextView address;
        public String mac_address;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.paired_device_list, parent, false));

            itemView.setOnClickListener(mDeviceClickListener);

            avator = (ImageView) itemView.findViewById(R.id.paired_device_list_img);
            name = (TextView) itemView.findViewById(R.id.paired_device_list_device_name);
            address = (TextView) itemView.findViewById(R.id.paired_device_list_device_address);
        }

        private View.OnClickListener mDeviceClickListener = new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(context, DeviceActivity.class);
                i.putExtra(Constants.EXTRA_DEVICE_ADDRESS, mac_address);
                startActivity(i);
            }
        };
    }


    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.
        private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        private Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        private final List<String> mDeviceName;
        private final List<String> mDeviceAddress;
        private final List<Drawable> mAvatar;

        public ContentAdapter(Context context) {
            mDeviceName = new ArrayList<>();
            mDeviceAddress = new ArrayList<>();
            mAvatar = new ArrayList<>();
            Resources resources = context.getResources();
            TypedArray a = resources.obtainTypedArray(R.array.place_avator);

            for (BluetoothDevice device : pairedDevices) {
                mDeviceName.add(device.getName());
                mDeviceAddress.add(device.getAddress());
                mAvatar.add(a.getDrawable(0));
            }
            a.recycle();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.avator.setImageDrawable(mAvatar.get(position));
            holder.name.setText(mDeviceName.get(position));
            String mac_address = mDeviceAddress.get(position);
            holder.address.setText(mac_address);
            holder.mac_address = mac_address;
        }

        @Override
        public int getItemCount() {
            return pairedDevices.size();
        }
    }

}