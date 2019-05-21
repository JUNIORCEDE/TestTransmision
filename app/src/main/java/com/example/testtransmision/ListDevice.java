package com.example.testtransmision;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.testtransmision.Broadcast.WifiDirectBroadcastReceiver;
import com.example.testtransmision.BtConnect.BluetoothConnect;

import java.util.ArrayList;
import java.util.Set;


public class ListDevice extends AppCompatActivity {

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    WifiP2pManager mWifiP2pManager;

    ListView deviceList;
    Bundle bundle = null;
    Boolean BtOn = false;


    BluetoothConnect bluetoothConnect = null;
    BluetoothAdapter BTAdapter = null;
    public static int REQUEST_BLUETOOTH = 1;

    public ArrayList<String> ListadoDispositivos = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_device);
        deviceList = (ListView)findViewById(R.id.deviceList);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bundle = this.getIntent().getExtras();
        if (bundle != null) {
            BtOn = bundle.getBoolean("BtOn");
        }

        if (BtOn){
            Log.i("BTnfo","bton");
            bluetoothConnect = new BluetoothConnect();
            BTAdapter = bluetoothConnect.getBluetoothAdapter();
            if (BTAdapter == null) {
                new AlertDialog.Builder(this)
                        .setTitle("Not compatible")
                        .setMessage("Your phone does not support Bluetooth")
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        }
        else{
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if(!wifiManager.isWifiEnabled())
                wifiManager.setWifiEnabled(true);

            mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
            if (mWifiP2pManager != null) {
                mChannel = mWifiP2pManager.initialize(this,  getMainLooper(), null);
                if (mChannel == null)  mWifiP2pManager = null;
            }

            mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(getApplicationContext(), getMainLooper(), null);
            mReceiver = WifiDirectBroadcastReceiver.createInstance();
            ((WifiDirectBroadcastReceiver) mReceiver).setListView(deviceList);
            ((WifiDirectBroadcastReceiver) mReceiver).setmManager(mManager);
            ((WifiDirectBroadcastReceiver) mReceiver).setmChannel(mChannel);

            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
            buscarWifi();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void buscarWifi(){
        try {
            Thread.sleep(300);
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ListDevice.this, "Buscando...", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {

                }
            });
        } catch (InterruptedException e) {
            Toast.makeText(ListDevice.this, "No se ha podido iniciar la búsqueda", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!BtOn) {
            registerReceiver(mReceiver, mIntentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!BtOn) {
            unregisterReceiver(mReceiver);
        }
    }

    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i("BTnfo","Onreceive");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                ListadoDispositivos.clear();
                ListadoDispositivos.add(device.getName()+ " - "+device.getAddress());
                Log.i("BTInfo",device.getName()+ " - "+device.getAddress());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,ListadoDispositivos);
            deviceList.setAdapter(adapter);
        }
    };
}
