package com.a.bluetoothlowenergy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.PhantomReference;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private BleMessagingService mBleService;
    private final int SELECT_DEVICE = 1;
    private final int REQUEST_ENABLE_BT = 2;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindBleService();
    }

    public void scann(View view) {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        DeviceListActivity.mBluetoothAdapter = bluetoothAdapter;
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else {
            Intent i = new Intent(this,DeviceListActivity.class);
            startActivityForResult(i, SELECT_DEVICE);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBleService = ((BleMessagingService.LocalBinder)service).getService();
            if (!mBleService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "BleMessagingService disconnected");
            mBleService = null;
        }
    };

    private BleBroadcastReceiver bleBroadcastReceiver = new BleBroadcastReceiver(new OnReceiveListener() {
        @Override
        public void onConnect() {
            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnect() {
            Toast.makeText(MainActivity.this, "Disconnect", Toast.LENGTH_SHORT).show();
            mBleService.close();
        }

        @Override
        public void onDataAvailable(String data) {
            Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onDataAvailable: "+data);
        }

        @Override
        public void onDataAvailable(byte[] data) {
            Toast.makeText(MainActivity.this, "sdad", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServicesDiscovered() {
            Toast.makeText(MainActivity.this, "ServicesDiscovered", Toast.LENGTH_SHORT).show();
            mBleService.enableNotification();
        }

        @Override
        public void onError(int errorCode) {
            Toast.makeText(MainActivity.this, "onError", Toast.LENGTH_SHORT).show();
            mBleService.disconnect();
        }
    });
    private void bindBleService(){
        Intent bindIntent = new Intent(this,BleMessagingService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(bleBroadcastReceiver,setIntentFilter());
    }

    private static IntentFilter setIntentFilter(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleMessagingService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleMessagingService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleMessagingService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleMessagingService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BleMessagingService.DEVICE_DOES_NOT_SUPPORT_BLE);
        return intentFilter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(this,DeviceListActivity.class);
                    startActivityForResult(i, SELECT_DEVICE);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                }
                break;
            case SELECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String deviceId = data.getStringExtra("mac");
                    mBleService.connect(deviceId);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if(mBleService !=null) {
            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(bleBroadcastReceiver);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            unbindService(serviceConnection);
            mBleService.stopSelf();
            mBleService = null;
        }
    }

    public void disconnect(View view) {
        mBleService.disconnect();
    }

    public void send(View view) {
        mBleService.writeCharacteristic("hi".getBytes());
    }
}
