# BluetoothLowEnergy
This App is used to scann the available lock , connect to the lock ,send data to lock and disconnect the device.
### Prerequisites
You have to add some permission in manifest as 
```
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

### Installing
```
For download the app use git clone https://github.com/imbichitra/BluetoothLowEnergy.git
```
### For read advertisement packet
If you want to read advertisement packet and perform some specific action then you can get it from the onLeScan as below
* onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord)
```
 byte[] string = {'B', 'L', 'E', 'N', 'A', 'M'};
                    for (int i = 9; i < 15; i++) {
                        if (scanRecord[i] != string[i - 9])
                            return;
                    } 
 ```
 In the above example i want to display specific ble device that contain  {'B', 'L', 'E', 'N', 'A', 'M'} otherwise you can comment it,if you comment the above code then all the available ble device is show in the scan list.
 
 ### Send data
This app send hi to ble and got hello from my ble device . You can customize it accordingly your choice.

### Note
ACCESS_FINE_LOCATION location is not given you can give it mannually 
