package com.example.recorder.controlador;

import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.recorder.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

//esta clase solo crea la barra de navegacion inferior

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //los fragments son los encargados de crear sus propios directorios de almacenado. Si se visita este fragment sin que se creen primero las carpetas, da error
        //creacion de la carpeta RecordedAudio en la raiz de la memoria interna. Se sobreescribe cada vex, pero no borra los datos que haya en el interior, con lo cual no hare un if file exist
        File nuevaCarpetaAudio = new File(Environment.getExternalStorageDirectory() + "/RecordedAudio");
        nuevaCarpetaAudio.mkdir();
        //creacion de la carpeta RecordedAudio en la raiz de la memoria interna. Se sobreescribe cada vex, pero no borra los datos que haya en el interior, con lo cual no hare un if file exist
        File nuevaCarpetaVideo = new File(Environment.getExternalStorageDirectory() + "/RecordedVideo");
        nuevaCarpetaVideo.mkdir();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_video, R.id.navigation_audio, R.id.navigation_library)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}
