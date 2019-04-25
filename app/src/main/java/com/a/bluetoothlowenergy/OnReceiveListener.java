package com.a.bluetoothlowenergy;

public interface OnReceiveListener {
    void onConnect();
    void onDisconnect();
    void onDataAvailable(String data);
    void onDataAvailable(byte[] data);
    void onServicesDiscovered();
    void onError(int errorCode);
}
