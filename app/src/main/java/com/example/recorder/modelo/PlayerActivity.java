package com.example.recorder.modelo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recorder.R;

import java.io.IOException;

import static android.widget.Toast.LENGTH_SHORT;

//esta clase es el reproductor multimedia para los audios de la lista

public class PlayerActivity extends AppCompatActivity {

    //variable para asignar un nombre al log
    private static final String LOG_TAG = "Reproductor Audio";

    //reproductor
    private MediaPlayer reproductor;

    //botones, barra de seguimiento y textos
    private Button botonReproducir, botonRebobinar, botonAvanzar, botonPararReproduccion;
    private SeekBar seekBar;
    private TextView seekProgress;
    private TextView seekDuration;

    //manejador de la barra de seguimiento
    Handler seekHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //controles en el layout
        seekBar = (SeekBar) findViewById(R.id.seekBarPlayer);
        seekProgress = (TextView) findViewById(R.id.textViewProgress);
        seekDuration = (TextView) findViewById(R.id.textViewDuration);

        //instancia de la clase reproductor al mostrar la actividad
        reproductor = new MediaPlayer();

        //obtencion del titulo de la cancion para cargarlo en el reproductor. Se obtiene recibiendola de la listview y se crea una ruta con el nombre
        Intent intent = getIntent();
        String titulo = intent.getStringExtra("titulo");
        final String fichero = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordedAudio/" + titulo;

        //asignar la ruta del fichero a leer y preparar el reproductor
        try {
            reproductor.setDataSource(fichero);
            reproductor.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //boton play
        botonReproducir = findViewById(R.id.botonReproducir);
        botonReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarReproductor();
            }
        });

        //boton stop (finalmente por estetica lo he cambiado a un boton de volver hacia atras
        botonPararReproduccion = findViewById(R.id.botonPararReproduccion);
        botonPararReproduccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pararReproductor();
            }
        });

        botonAvanzar = findViewById(R.id.botonAvanzar);
        botonAvanzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                avanzarReproductor();
            }
        });

        botonRebobinar = findViewById(R.id.botonRebobinar);
        botonRebobinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rebobinarReproductor();
            }
        });


    }

    //reproducir el audio grabado que se carga el archivo que recibe del intent construyendo la ruta correcta
    public void iniciarReproductor() {

        //si no esta reproduciendo se inicia. con esto se consigue que al pulsar otra vez se pause el audio y se pueda reanudar al pulsar de nuevo
        if (!reproductor.isPlaying()) {
            //iniciar reproductor
            reproductor.start();
            //se obtiene la duracion del audio
            seekBar.setMax(reproductor.getDuration());
            //se obtiene el progreso para actualizar los textview con el tiempo
            getProgress();
            //cambiar el icono a pause
            botonReproducir.setBackground(getDrawable(R.drawable.twotone_pause_circle_filled_black_18dp));
            Toast.makeText(getApplication(), "Reproduciendo Audio", Toast.LENGTH_SHORT).show();
        } else {
            //pausar reproductor
            reproductor.pause();
            //cambiar icono a play
            botonReproducir.setBackground(getDrawable(R.drawable.twotone_play_circle_filled_black_18dp));
            Toast.makeText(getApplication(), "Reproductor en pausa", Toast.LENGTH_SHORT).show();
        }
    }

    //parar la reproduccion de audio
    private void pararReproductor() {

        reproductor.stop();
        finish();
        Toast.makeText(getApplication(), "Audio parado", Toast.LENGTH_SHORT).show();
        //se deshabilita el boton de parada
        botonPararReproduccion.setEnabled(false);
    }

    //rebobinar
    public void rebobinarReproductor() {

        int currentPosition = reproductor.getCurrentPosition();
        int duration = reproductor.getDuration();
        int recoil = 10000;
        Toast.makeText(PlayerActivity.this, "-10 seconds", LENGTH_SHORT).show();

        if (currentPosition - recoil > 0) {
            reproductor.seekTo(currentPosition - recoil);
        }
    }

    //avanzar
    public void avanzarReproductor() {

        int currentPosition = reproductor.getCurrentPosition();
        int duration = reproductor.getDuration();
        int advance = 10000;
        Toast.makeText(PlayerActivity.this, "+10 seconds", LENGTH_SHORT).show();

        if (currentPosition + advance < duration) {
            reproductor.seekTo(currentPosition + advance);
        }
    }

    //hilo para hacer mover al barra de seguimiento segun progreso del audio
    Runnable run = new Runnable() {
        @Override
        public void run() {

            getProgress();

            //ESTO PERMITE QUE AL PULSAR EN LA BARRA TE DIRIJA A ESE PUNTO
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        reproductor.seekTo(progress);
                        seekBar.setProgress(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    };

    public void getProgress() {

        //PARA QUE LA BARRA SE VAYA ACTUALIZANDO HAY QUE METERLA EN EL RUNNABLE
        seekBar.setProgress(reproductor.getCurrentPosition());
        seekHandler.postDelayed(run, 1000);

        //OBTIENE LA POSICION ACTUAL Y LA DURACION TOTAL DE LA CANCION CONVERTIDA A MINUTOS-SEGUNDOS
        int currentPositionMin = reproductor.getCurrentPosition() / 1000 / 60;
        int currentPositionSec = reproductor.getCurrentPosition() / 1000 % 60;
        int durationMin = reproductor.getDuration() / 1000 / 60;
        int durationSec = reproductor.getDuration() / 1000 % 60;

        String currentMinutes;
        String currentSeconds;
        String durationMinutes;
        String durationSeconds;

        //CON ESTO AÑADO EL FORMATO 00:00, AÑADIENDO CERO DELANTE AL STRING
        if (currentPositionMin < 10) {
            currentMinutes = "0" + currentPositionMin;
        } else {
            currentMinutes = "" + currentPositionMin;
        }

        if (currentPositionSec < 10) {
            currentSeconds = "0" + currentPositionSec;
        } else {
            currentSeconds = "" + currentPositionSec;
        }

        if (durationMin < 10) {
            durationMinutes = "0" + durationMin;
        } else {
            durationMinutes = "" + durationMin;
        }

        if (durationSec < 10) {
            durationSeconds = "0" + durationSec;
        } else {
            durationSeconds = "" + durationSec;
        }

        seekProgress.setText(currentMinutes + ":" + currentSeconds);
        seekDuration.setText(durationMinutes + ":" + durationSeconds);
    }


}
