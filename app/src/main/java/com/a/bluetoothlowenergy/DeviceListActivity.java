package com.a.bluetoothlowenergy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceListActivity extends AppCompatActivity {

    public static final String TAG = DeviceListActivity.class.getSimpleName();
    private static final long SCAN_PERIOD = 15000;
    Map<String, Integer> devRssiValues;
    List<BluetoothDevice> deviceList;
    private DeviceAdapter deviceAdapter;
    private TextView mEmptyList;
    public static BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        mEmptyList = (TextView) findViewById(R.id.empty);
        mHandler = new Handler();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth Low Energy not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        populateList();
    }

    private void populateList() {
        /* Initialize device list container */
        deviceList = new ArrayList<BluetoothDevice>();
        devRssiValues = new HashMap<String, Integer>();
        deviceAdapter = new DeviceAdapter(this, deviceList,devRssiValues);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView newDevicesListView = findViewById(R.id.new_devices);
        newDevicesListView.setLayoutManager(layoutManager);
        newDevicesListView.setAdapter(deviceAdapter);
        deviceAdapter.setOnItemClickListener(new DeviceAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                Toast.makeText(DeviceListActivity.this, deviceList.get(position).getAddress(), Toast.LENGTH_SHORT).show();
                Intent result = new Intent();
                result.putExtra("mac",deviceList.get(position).getAddress());
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });
        scanLeDevice(true);
    }

    private void scanLeDevice(boolean b) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mEmptyList.setText("No device Found");
                Log.d(TAG, "run: "+deviceList);
            }
        }, SCAN_PERIOD);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //boolean equal = true;
                    byte[] string = {'A', 'Z', 'L', 'O', 'C', 'K'};
                    for (int i = 9; i < 15; i++) {
                        if (scanRecord[i] != string[i - 9])
                            return;
                    }
                    Log.d(TAG, "run: "+device);
                    addDevice(device, rssi);
                }
            });
        }
    };

    private void addDevice(BluetoothDevice device, int rssi) {
        boolean deviceFound = false;

        for (BluetoothDevice listDev : deviceList) {
            if (listDev.getAddress().equals(device.getAddress())) {
                deviceFound = true;
                break;
            }
        }
        devRssiValues.put(device.getAddress(), rssi);
        if (!deviceFound) {
            deviceList.add(device);
            mEmptyList.setVisibility(View.GONE);
            deviceAdapter.notifyDataSetChanged();
        }
    }
}
