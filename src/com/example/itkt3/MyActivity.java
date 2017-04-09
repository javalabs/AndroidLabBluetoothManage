package com.example.itkt3;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     * http://stackoverflow.com/questions/10795424/how-to-get-the-bluetooth-devices-as-a-list
     * https://developer.android.com/reference/android/bluetooth/BluetoothAdapter.html
     */

    private Switch aSwitch;
    private TextView aTextView;
    private ListView alistView;
    private ArrayList<String> mDeviceList = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter;


    private BluetoothAdapter ba;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ba = BluetoothAdapter.getDefaultAdapter();

        aSwitch = (Switch)findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Toast.makeText(MyActivity.this, "Change switch: " + isChecked, Toast.LENGTH_LONG).show();
                if(isChecked) {
                    ba.enable();
                }
                else {
                    ba.disable();
                }
            }
        });
        aSwitch.setChecked(ba.isEnabled());
        aTextView = (TextView)findViewById(R.id.textView1);
        alistView = (ListView) findViewById(R.id.listView1);
        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mDeviceList);
        alistView.setAdapter(mAdapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                mAdapter.notifyDataSetChanged();

            }

        }
    };

    public void onBtnClick(View v) {
        switch (v.getId()) {
            case R.id.button1: { // Характеристики Bluetooth
                HashMap<Integer, String> hashMap = new HashMap<>(3);
                hashMap.put(BluetoothAdapter.SCAN_MODE_CONNECTABLE, "CONNECTABLE");
                hashMap.put(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, "DISCOVERABLE");
                hashMap.put(BluetoothAdapter.SCAN_MODE_NONE, "NONE");

                String adress = ba.getAddress();
                String name = ba.getName();
                int scanMode = ba.getScanMode();

                aTextView.setText(String.format("Название: %s\nАдрес: %s\nScanMode: %s",
                                                name, adress, hashMap.get(scanMode)));
                break;
            }
            case R.id.button2: { // Поиск устройств Bluetooth
                if (!ba.isDiscovering()) {
                    Toast.makeText(this, "Поиск устройств...", Toast.LENGTH_SHORT).show();
                    mDeviceList.clear();
                    mAdapter.notifyDataSetChanged();
                    ba.startDiscovery();
                }
                break;
            }
        }
    }
}
