package com.example.testtransmision.BtConnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Build;
import android.widget.Toast;

import java.util.Set;

public class BluetoothConnect {
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> Dispositivos;
    private BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

    public BluetoothConnect() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    // Comprueba que el dispositivo admite bluetooth
    public boolean comprobarBluetooth(){
        boolean soportaBluetooth = true;
        if ( bluetooth == null) {
            soportaBluetooth = false;
        }
        return soportaBluetooth;
    }

    // Habilita el bluetooth en caso de no estarlo
    public void habilitarBluetooth(){
        if (!bluetooth.isEnabled()){
            bluetooth.enable();
        }
    }

    public void desHabilitarBluetooth(){
        if (bluetooth.isEnabled()){
            bluetooth.disable();
        }
    }

    // Retorna la lista de dispositivos emparejados.
    public Set<BluetoothDevice> getListContactBluetooth (){
        Dispositivos =  this.bluetooth.getBondedDevices();
        return Dispositivos;
    }

}
