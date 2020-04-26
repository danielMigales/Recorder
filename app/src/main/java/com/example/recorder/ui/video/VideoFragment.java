package com.example.recorder.ui.video;


import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.recorder.R;

import java.io.IOException;

public class VideoFragment extends Fragment {

    private VideoViewModel videoViewModel;

    MediaRecorder mediaRecorder = new MediaRecorder();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        videoViewModel = ViewModelProviders.of(this).get(VideoViewModel.class);
        View view = inflater.inflate(R.layout.fragment_video, container, false);


        SurfaceView surface = view.findViewById(R.id.surfaceView);
        SurfaceHolder holder = surface.getHolder();
        holder.addCallback((SurfaceHolder.Callback) this);

        holder.setFixedSize(400, 300);


        mediaRecorder.start();
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();


        return view;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
                mediaRecorder.setOutputFile("/sdcard/myoutputfile.mp4");
                // Asociando la previsualización a la superficie
                mediaRecorder.setPreviewDisplay(holder.getSurface());
                mediaRecorder.prepare();
            } catch (IllegalArgumentException e) {
                Log.d("MEDIA_PLAYER", e.getMessage());
            } catch (IllegalStateException e) {
                Log.d("MEDIA_PLAYER", e.getMessage());
            } catch (IOException e) {
                Log.d("MEDIA_PLAYER", e.getMessage());
            }
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mediaRecorder.release();
    }

    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
    }
}