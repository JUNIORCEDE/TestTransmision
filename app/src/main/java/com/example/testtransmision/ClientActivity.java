package com.example.testtransmision;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;

public class ClientActivity extends AppCompatActivity {
    Switch sw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        sw = (Switch)findViewById(R.id.CambioTecno);
        
    }
}
