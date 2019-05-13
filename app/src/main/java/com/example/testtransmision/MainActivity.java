package com.example.testtransmision;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    Button BtnFile, BtnInicio;
    TextView TxtFile;
    private final int PICKER = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BtnFile = (Button)findViewById(R.id.btnFile);
        TxtFile = (TextView)findViewById(R.id.txtFile);
        BtnInicio = (Button)findViewById(R.id.btnInicio);

        BtnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectFile();
            }
        });

        BtnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Result.class);
                startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    private void SelectFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try{
            startActivityForResult(Intent.createChooser(intent,"Seleccione el archivo que desea enviar"),PICKER);
        }
        catch(android.content.ActivityNotFoundException ex){
            Toast.makeText(this, "Por favor, instale un administrador de archivos", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case PICKER:
                if (resultCode == RESULT_OK){
                    String FilePath = data.getData().getPath();
                    File file = new File(FilePath);
                    TxtFile.setText(FilePath);
                }
        }
    }
}
