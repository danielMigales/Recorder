package com.example.recorder.ui.audio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.recorder.R;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioFragment extends Fragment {

    private AudioViewModel audioViewModel;

    //etiqueta para identificar la aplicacion en el log
    private static final String LOG_TAG = "Grabadora_Audio";

    //objetos de las clases MediaRecorder y MediaPlayer para la grabacion y reproduccion
    private MediaRecorder grabadora;
    private MediaPlayer reproductor;

    //variable que identifica el fichero donde se guardara la grabacion y su extension. Se guardara en la carpeta RecordedAudio que se crea en el onCreate
    String fichero = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordedAudio/" + fecha() + ".3gp";

    //variables de los botones del layout
    private Button botonGrabar, botonReproducir, botonPararGrabacion, botonPararReproduccion;
    private Chronometer simpleChronometer;

    // esto sirve para pedir permiso para grabar audio y grabarlo en la memoria
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToWriteAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) getActivity().finish();
        Toast.makeText(getContext(), "No tienes permiso para grabar", Toast.LENGTH_SHORT).show();

        if (!permissionToWriteAccepted) getActivity().finish();
        Toast.makeText(getContext(), "No tienes permiso para guardar", Toast.LENGTH_SHORT).show();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        audioViewModel = ViewModelProviders.of(this).get(AudioViewModel.class);
        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        //pedir aceptar permisos al usuario
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_WRITE_STORAGE_PERMISSION);

        //creacion de la carpeta RecordedAudio en la raiz de la memoria interna. Se sobreescribe cada vex, pero no borra los datos que haya en el interior, con lo cual no hare un if file exist
        File nuevaCarpeta = new File(Environment.getExternalStorageDirectory() + "/RecordedAudio");
        nuevaCarpeta.mkdir();


        //inicializacion de botones. El de reproducir y parar se desactivan hasta que no se necesiten sus funciones
        botonReproducir = view.findViewById(R.id.botonReproducir);
        botonReproducir.setEnabled(true);
        botonGrabar = view.findViewById(R.id.botonGrabar);
        botonPararGrabacion = view.findViewById(R.id.botonPararGrabacion);
        botonPararGrabacion.setEnabled(false);
        botonPararReproduccion = view.findViewById(R.id.botonPararReproduccion);
        botonPararReproduccion.setEnabled(false);
        simpleChronometer = view.findViewById(R.id.simpleChronometer);

        botonReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarReproductor();
                Toast.makeText(getContext(), "Reproduciendo Audio", Toast.LENGTH_SHORT).show();
            }
        });

        botonGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarGrabadora();
                simpleChronometer.start();
                Toast.makeText(getContext(), "Grabacion Iniciada", Toast.LENGTH_SHORT).show();
            }

        });

        botonPararGrabacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pararGrabadora();
                fecha();
                simpleChronometer.stop();
                simpleChronometer.setBase(SystemClock.elapsedRealtime());
                Toast.makeText(getContext(), "Grabacion Finalizada", Toast.LENGTH_SHORT).show();
            }
        });

        botonPararReproduccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pararReproductor();
                Toast.makeText(getContext(), "Audio parado", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    //procedimiento de grabacion de audio
    public void iniciarGrabadora() {

        grabadora = new MediaRecorder();  //se instancia la clase
        grabadora.setAudioSource(MediaRecorder.AudioSource.MIC); //la fuente de audio vendra del microfono del dispositivo
        grabadora.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //el formato de grabacion sera 3gp
        grabadora.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); //el audio se codificara usando este formato
        grabadora.setOutputFile(fichero); //se le añade el fichero de salida

        botonGrabar.setEnabled(false);
        botonPararGrabacion.setEnabled(true);
        botonReproducir.setEnabled(false);

        try {
            grabadora.prepare();
        } catch (IOException ex) { //excepcion al no encontrar archivo
            Log.e(LOG_TAG, "Fallo en la Grabacion");
        }
        grabadora.start();
    }

    //procedimiento para parar la grabacion
    public void pararGrabadora() {

        grabadora.stop(); //parar
        grabadora.release(); //liberar todos los procesos

        botonGrabar.setEnabled(true);
        botonPararGrabacion.setEnabled(false);
        botonReproducir.setEnabled(true);
    }

    //reproducir el audio grabado
    public void iniciarReproductor() {

        reproductor = new MediaPlayer(); //instancia de la clase reproductor

        try {
            reproductor.setDataSource(fichero); //asignamos la ruta del fichero a leer
            reproductor.prepare();
            reproductor.start();

            botonGrabar.setEnabled(false);
            botonPararGrabacion.setEnabled(false);
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

        botonGrabar.setEnabled(true);
        botonPararGrabacion.setEnabled(false);
        botonReproducir.setEnabled(true);
        botonPararReproduccion.setEnabled(false);
    }

    //obtiene la fecha y hora para nombrar al archivo de audio
    private String fecha() {

        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("HH-mm_dd-MM-yyyy");
        String fecha = hourdateFormat.format(date);
        System.out.println(fecha);
        return fecha;
    }

}
