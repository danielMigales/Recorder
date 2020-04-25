package com.example.recorder.ui.audio;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recorder.R;

import java.io.IOException;

//esta clase es el reproductor multimedia para los audios de la lista

public class PlayerActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Reproductor Audio";
    private MediaPlayer reproductor;
    private Button botonReproducir, botonPararReproduccion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //controles
        botonReproducir = findViewById(R.id.botonReproducir);
        botonReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarReproductor();
                Toast.makeText(getApplication(), "Reproduciendo Audio", Toast.LENGTH_SHORT).show();
            }
        });

        botonPararReproduccion = findViewById(R.id.botonPararReproduccion);
        botonPararReproduccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pararReproductor();
                Toast.makeText(getApplication(), "Audio parado", Toast.LENGTH_SHORT).show();
            }
        });


    }


    //reproducir el audio grabado que se carga el archivo que recibe del intent construyendo la ruta correcta
    public void iniciarReproductor() {

        reproductor = new MediaPlayer(); //instancia de la clase reproductor
        Intent intent = getIntent();
        String titulo = intent.getStringExtra("titulo");
        System.out.println(titulo);
        String fichero = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordedAudio/" + titulo;

        try {
            reproductor.setDataSource(fichero); //asignamos la ruta del fichero a leer
            reproductor.prepare();
            reproductor.start();

            botonReproducir.setEnabled(false);
            botonPararReproduccion.setEnabled(true);

        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e(LOG_TAG, "Fallo en la reproduccion");
        }
    }

    //parar la reproduccion de audio
    private void pararReproductor() {

        reproductor.stop();
        reproductor.release();
        reproductor = null;

        botonReproducir.setEnabled(true);
        botonPararReproduccion.setEnabled(false);
    }

}
