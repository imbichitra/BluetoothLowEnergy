package com.a.bluetoothlowenergy;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyViewHolder>{

    public static final String TAG = DeviceAdapter.class.getSimpleName();
    private Context context;
    private List<BluetoothDevice> deviceList;
    private Map<String, Integer> devRssiValues;
    private OnRecyclerViewItemClickListener mItemClickListener;
    DeviceAdapter(Context context, List<BluetoothDevice> deviceList, Map<String, Integer> devRssiValues){
        this.context = context;
        this.deviceList = deviceList;
        this.devRssiValues = devRssiValues;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.device_list_item,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.device_name.setText(deviceList.get(i).getName());
        try {
            String deviceId = deviceList.get(i).getAddress();
            Log.d(TAG, "onBindViewHolder: "+deviceId);
            myViewHolder.rssi.setText(String.valueOf(devRssiValues.get(deviceId)));
        }catch (Exception e){
            e.printStackTrace();
        }

        myViewHolder.mac_id.setText(deviceList.get(i).getAddress());
        Log.d(TAG, "onBindViewHolder: rsii"+devRssiValues);
        myViewHolder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickListener(v, i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView device_name,rssi,mac_id;
        LinearLayout main;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            device_name = itemView.findViewById(R.id.device_name);
            rssi = itemView.findViewById(R.id.rssi);
            mac_id = itemView.findViewById(R.id.mac_id);
            main = itemView.findViewById(R.id.main);
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }
    public interface OnRecyclerViewItemClickListener{
        void onItemClickListener(View view,int position);
    }
}
