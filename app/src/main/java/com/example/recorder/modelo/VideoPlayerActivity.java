package com.example.recorder.modelo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recorder.R;

public class VideoPlayerActivity extends AppCompatActivity {

    //la superficie y el mediaplayer
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private VideoView videoView;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        getSupportActionBar().hide();
        //QUITA LA BARRA DE NOTIFICACIONES
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mediaPlayer = new MediaPlayer();
        videoView = findViewById(R.id.videoView);

        //obtencion del titulo para cargarlo en el reproductor.
        Intent intent = getIntent();
        String titulo = intent.getStringExtra("titulo");
        String fichero = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Recordedvideo/" + titulo;

        videoView.setVideoPath(fichero);

        // BARRA CON BOTONES QUE APARECE AL TOCAR EL VIDEO
        if (mediaController == null) {
            mediaController = new MediaController(VideoPlayerActivity.this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
        }

        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer player) {
                videoView.seekTo(position);
                if (position == 0) {
                    videoView.start();
                }

                player.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        //ESTO HACE QUE LA BARRA APAREZCA EN EL MISMO VIDEO EN VEZ DE ABAJO
                        mediaController.setAnchorView(videoView);
                    }
                });
            }
        });


    }

}

