package com.example.testtransmision.Broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Activity mActivity;

    private List<String> peersName = new ArrayList<String>();
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    private static WifiDirectBroadcastReceiver instance;

    public ArrayList<String> listado = new ArrayList<String>();
    WifiP2pDevice[] deviceArray;

    ListView Listado = null;



    private InetAddress ownerAddr;
    private int isGroupeOwner;
    public static final int IS_OWNER = 1;
    public static final int IS_CLIENT = 2;

    private WifiDirectBroadcastReceiver(){
        super();
    }

    public static WifiDirectBroadcastReceiver createInstance(){
        if(instance == null) instance = new WifiDirectBroadcastReceiver();
        return instance;
    }

    public void setmManager(WifiP2pManager mManager) { this.mManager = mManager; }
    public void setmChannel(WifiP2pManager.Channel mChannel) { this.mChannel = mChannel; }
    public void setmActivity(Activity mActivity) { this.mActivity = mActivity; }
    public void setListView(ListView lista) { this.Listado = lista; }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Toast.makeText(context, "OnReceive "+action, Toast.LENGTH_SHORT).show();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                //Toast.makeText(context, "Wifi Encendido", Toast.LENGTH_SHORT).show();
            }
            else {
                //Toast.makeText(context, "Wifi Apagado", Toast.LENGTH_SHORT).show();
            }
        }
        else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if (mManager != null) {
                mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peerList) {
                        if (!peerList.getDeviceList().equals(peers)) {
                            peers.clear();
                            listado.clear();
                            peers.addAll(peerList.getDeviceList());
                            deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                            int index = 0;
                            for (WifiP2pDevice device : peerList.getDeviceList()) {
                                if (device.primaryDeviceType.equals("10-0050F204-5")) {
                                    listado.add(device.deviceName+" - "+device.deviceAddress);
                                    deviceArray[index] = device;
                                    index++;
                                }
                            }
                            if (Listado != null) {
                                Toast.makeText(mActivity.getApplicationContext(), "Llenando Lista", Toast.LENGTH_SHORT).show();
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity.getApplicationContext(),android.R.layout.simple_list_item_1,listado);
                                Listado.setAdapter(adapter);
                                Listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Toast.makeText(mActivity.getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();
                                        Toast.makeText(mActivity.getApplicationContext(), ""+deviceArray[position].deviceName, Toast.LENGTH_SHORT).show();
                                        final WifiP2pDevice dispositivo = deviceArray[position];
                                        WifiP2pConfig config = new WifiP2pConfig();
                                        config.deviceAddress = dispositivo.deviceAddress;
                                        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                                            @Override
                                            public void onSuccess() {
                                                Toast.makeText(mActivity.getApplicationContext(), "Conectado con "+ dispositivo.deviceName, Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(int reason) {
                                                Toast.makeText(mActivity.getApplicationContext(), "Error al conectar con "+ dispositivo.deviceName, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });
                            }
                        }
                        if (peers.size() == 0) {
                            Toast.makeText(mActivity.getApplicationContext(), "No se encontraron dispositivos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                //Toast.makeText(context, "mManage null", Toast.LENGTH_SHORT).show();
            }
        }
        else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            if(mManager == null) return;
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected()){
                Toast.makeText(mActivity.getApplicationContext(), "Conectados", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(mActivity.getApplicationContext(), "No Conectados", Toast.LENGTH_SHORT).show();
            }

        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
    }
}
