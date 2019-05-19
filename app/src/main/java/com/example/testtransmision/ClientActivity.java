package com.example.testtransmision;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testtransmision.Broadcast.WifiDirectBroadcastReceiver;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ClientActivity extends AppCompatActivity {
    Switch sw;
    Boolean activado = false;
    Button btnTesteo;
    TextView txtWifi, txtBt,infoDevice;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    Boolean ActivarTesteo = false;
    String nameDevice = "";
    Bundle bundle = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        sw = (Switch)findViewById(R.id.CambioTecno);
        txtWifi = (TextView)findViewById(R.id.TxtWifi);
        txtBt = (TextView)findViewById(R.id.TxtBt);
        infoDevice = (TextView)findViewById(R.id.nameDevice);
        btnTesteo = (Button) findViewById(R.id.btnTest);
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this,getMainLooper(),null);
        mReceiver = WifiDirectBroadcastReceiver.createInstance();
        ((WifiDirectBroadcastReceiver) mReceiver).setmManager(mManager);
        ((WifiDirectBroadcastReceiver) mReceiver).setmChannel(mChannel);
        ((WifiDirectBroadcastReceiver) mReceiver).setmActivity(this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        activado = sw.isChecked();
        if (activado){
            BTOn();
        }
        else{
            WifiOn();
        }

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    BTOn();
                }
                else{WifiOn();}
            }
        });

        bundle = getIntent().getExtras();
        if (bundle != null) {
            ActivarTesteo = bundle.getBoolean("btnTestEnable");
            nameDevice = bundle.getString("nameDevice");
        }
        Toast.makeText(this, nameDevice, Toast.LENGTH_SHORT).show();
        infoDevice.setText(R.string.conectado_con+" "+nameDevice);
        btnTesteo.setEnabled(ActivarTesteo);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void WifiOn(){
        txtBt.setTextColor(getResources().getColor(R.color.Rojo));
        txtWifi.setTextColor(getResources().getColor(R.color.Verde));
        //Toast.makeText(this, "Encendiendo Wifi Direct", Toast.LENGTH_SHORT).show();
        if (!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
            try {
                Thread.sleep(300);
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int reason) {

                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void BTOn(){
        txtWifi.setTextColor(getResources().getColor(R.color.Rojo));
        txtBt.setTextColor(getResources().getColor(R.color.Verde));
        if (wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
        }
    }

    public void Conectar(View v){
        Intent intent = new Intent (getApplicationContext(), ListDevice.class);
        startActivityForResult(intent, 0);
    }

    public void IrTesteo(View v){
        Intent intent = new Intent (getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, 0);
    }
}
