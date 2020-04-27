package com.example.recorder.ui.video;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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

public class VideoFragment extends Fragment {

    private VideoViewModel videoViewModel;

    //instancia de la clase grabadorra
    MediaRecorder grabadoraVideo = new MediaRecorder();

    //camara y superficie de la vista previa de video
    private Camera camara;
    private SurfaceView surface;
    private SurfaceHolder holder;

    //botones de control
    private Button botonGrabarVideo, botonPararGrabacion;
    private Chronometer temporizador;

    //variable que identifica el fichero donde se guardara el video creado. Se guardara en la carpeta RecordedVideo que se crea en el onCreate
    String fichero = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordedVideo/" + fecha() + ".mp4";

    // esto sirve para pedir permiso para grabar audio y grabarlo en la memoria
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        videoViewModel = ViewModelProviders.of(this).get(VideoViewModel.class);
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        //ESTO LO HE TRASLADADO AL MAIN
        //creacion de la carpeta RecordedAudio en la raiz de la memoria interna. Se sobreescribe cada vex, pero no borra los datos que haya en el interior, con lo cual no hare un if file exist
        //File nuevaCarpeta = new File(Environment.getExternalStorageDirectory() + "/RecordedVideo");
        //nuevaCarpeta.mkdir();

        //inicializacion de la superfice para ver la preview del video mientras graba
        surface = view.findViewById(R.id.surfaceView);

        //contador de tiempo
        temporizador = view.findViewById(R.id.cronometroVideo);

        //pedir aceptar permisos al usuario para grabar audio, grabar video y guardarlo en la memoria
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_WRITE_STORAGE_PERMISSION);

        //inicializacion de botones.
        botonGrabarVideo = view.findViewById(R.id.botonGrabarVideo);
        botonGrabarVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepararGrabadoraVideo(); //funcion que prepara todo el formato del video
                temporizador.start(); //inicio del cronometro
                Toast.makeText(getContext(), "Grabando video", Toast.LENGTH_SHORT).show();
            }
        });

        botonPararGrabacion = view.findViewById(R.id.botonPararGrabacionVideo);
        botonPararGrabacion.setEnabled(false);
        botonPararGrabacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pararGrabacionVideo(); //funcion que efectua la parada del video
                temporizador.stop(); //parar cronometro
                temporizador.setBase(SystemClock.elapsedRealtime()); //volver a poner crono a 0
                Toast.makeText(getContext(), "Grabacion Finalizada", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    //permisos de la aplicacion
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
        Toast.makeText(getContext(), "No tienes permiso para grabar audio", Toast.LENGTH_SHORT).show();

        if (!permissionToWriteAccepted) getActivity().finish();
        Toast.makeText(getContext(), "No tienes permiso para guardar", Toast.LENGTH_SHORT).show();
    }

    //prepatativos del video (formato, fuente..)
    public void prepararGrabadoraVideo() {

        botonGrabarVideo.setEnabled(false);
        botonPararGrabacion.setEnabled(true);

        //ruta para guardar el video
        grabadoraVideo.setOutputFile(fichero);
        //fuente y formatos de video
        grabadoraVideo.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        grabadoraVideo.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        grabadoraVideo.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        //grabadoraVideo.setVideoEncodingBitRate(512 * 1000);

        //formatos de audio
        //grabadoraVideo.setAudioSource(MediaRecorder.AudioSource.MIC);
        //grabadoraVideo.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        //duracion del video
        grabadoraVideo.setMaxDuration(10000);

        // Asociando la previsualización a la superficie
        holder = surface.getHolder();
        holder.setFixedSize(409, 657);
        grabadoraVideo.setPreviewDisplay(holder.getSurface());

        try {
            grabadoraVideo.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        grabadoraVideo.start();


    }

    //parada del video y reseteo
    public void pararGrabacionVideo() {

        //botones inhabilitados segun su necesidad
        botonPararGrabacion.setEnabled(false);
        botonGrabarVideo.setEnabled(true);

        grabadoraVideo.stop();
        grabadoraVideo.reset();
        Toast.makeText(getContext(), "Video guardado", Toast.LENGTH_SHORT).show();

    }

    //obtiene la fecha y hora para nombrar al archivo de audio
    private String fecha() {

        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("HH-mm-ss_dd-MM-yyyy");
        String fecha = hourdateFormat.format(date);
        return fecha;
    }
}