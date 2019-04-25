package com.a.bluetoothlowenergy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BleBroadcastReceiver extends BroadcastReceiver {
    private OnReceiveListener onReceiveListener;
    BleBroadcastReceiver(OnReceiveListener onReceiveListener){
        this.onReceiveListener = onReceiveListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        try {
            assert action != null;
            switch (action){
                case BleMessagingService.ACTION_GATT_CONNECTED:
                    onReceiveListener.onConnect();
                    break;
                case BleMessagingService.ACTION_GATT_DISCONNECTED:
                    onReceiveListener.onDisconnect();
                    break;
                case BleMessagingService.ACTION_GATT_SERVICES_DISCOVERED:
                    onReceiveListener.onServicesDiscovered();
                    break;
                case BleMessagingService.ACTION_DATA_AVAILABLE:
                    final byte[] txValue = intent.getByteArrayExtra(BleMessagingService.EXTRA_DATA);
                    try {
                        String text = new String(txValue, "ISO-8859-1");
                        onReceiveListener.onDataAvailable(text);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case BleMessagingService.DEVICE_DOES_NOT_SUPPORT_BLE:
                    onReceiveListener.onError(1);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
