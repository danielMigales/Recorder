package com.example.recorder.ui.audio;

import android.Manifest;
import android.content.pm.PackageManager;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//este fragment es el que tendra la grabadora de audio

public class AudioFragment extends Fragment {

    private AudioViewModel audioViewModel;

    //etiqueta para identificar la aplicacion en el log
    private static final String LOG_TAG = "Grabadora_Audio";

    //objetos de las clases MediaRecorder y MediaPlayer para la grabacion y reproduccion
    private MediaRecorder grabadoraAudio;

    //variable que identifica el fichero donde se guardara la grabacion y su extension. Se guardara en la carpeta RecordedAudio que se crea en el onCreate
    String fichero = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordedAudio/" + fecha() + ".3gp";

    //variables de los botones del layout
    private Button botonGrabar, botonPararGrabacion;
    private Chronometer temporizador;

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

        //ESTO LO HE TRASLADADO AL MAIN
        // creacion de la carpeta RecordedAudio en la raiz de la memoria interna. Se sobreescribe cada vex, pero no borra los datos que haya en el interior, con lo cual no hare un if file exist
        //File nuevaCarpeta = new File(Environment.getExternalStorageDirectory() + "/RecordedAudio");
        //nuevaCarpeta.mkdir();

        //inicializacion de botones.
        botonGrabar = view.findViewById(R.id.botonGrabarAudio);
        botonPararGrabacion = view.findViewById(R.id.botonPararGrabacionAudio);
        botonPararGrabacion.setEnabled(false);
        temporizador = view.findViewById(R.id.cronometroAudio);

        botonGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarGrabadora();
                temporizador.start();
                Toast.makeText(getContext(), "Grabacion Iniciada", Toast.LENGTH_SHORT).show();
            }

        });

        botonPararGrabacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pararGrabadora();
                fecha();
                temporizador.stop();
                temporizador.setBase(SystemClock.elapsedRealtime());
                Toast.makeText(getContext(), "Grabacion Finalizada", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    //procedimiento de grabacion de audio
    public void iniciarGrabadora() {

        grabadoraAudio = new MediaRecorder();  //se instancia la clase
        grabadoraAudio.setAudioSource(MediaRecorder.AudioSource.MIC); //la fuente de audio vendra del microfono del dispositivo
        grabadoraAudio.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //el formato de grabacion sera 3gp
        grabadoraAudio.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); //el audio se codificara usando este formato
        grabadoraAudio.setOutputFile(fichero); //se le añade el fichero de salida
        botonGrabar.setEnabled(false);
        botonPararGrabacion.setEnabled(true);

        try {
            grabadoraAudio.prepare();
        } catch (IOException ex) { //excepcion al no encontrar archivo
            Log.e(LOG_TAG, "Fallo en la Grabacion");
        }
        grabadoraAudio.start();
    }

    //procedimiento para parar la grabacion
    public void pararGrabadora() {

        grabadoraAudio.stop(); //parar
        grabadoraAudio.release(); //liberar todos los procesos
        botonGrabar.setEnabled(true);
        botonPararGrabacion.setEnabled(false);
    }

    //obtiene la fecha y hora para nombrar al archivo de audio
    public String fecha() {

        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("HH-mm-ss_dd-MM-yyyy");
        String fecha = hourdateFormat.format(date);
        return fecha;
    }

}
