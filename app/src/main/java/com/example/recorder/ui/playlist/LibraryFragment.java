package com.example.recorder.ui.playlist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.recorder.R;
import com.example.recorder.controlador.AdaptadorArchivo;
import com.example.recorder.controlador.Archivo;
import com.example.recorder.modelo.PlayerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//este fragment es el que tiene el listado de audios encontrados en la carpeta que se ha creado

public class LibraryFragment extends Fragment {

    private LibraryViewModel libraryViewModel;

    //se declara un arraylist y un listview
    private ArrayList<Archivo> listadoArchivos;
    private ListView listViewArchivos;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        libraryViewModel = ViewModelProviders.of(this).get(LibraryViewModel.class);
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        listViewArchivos = view.findViewById(R.id.listadoArchivos);
        listadoArchivos = new ArrayList<Archivo>();

        //llamada al metodo que obtiene los datos para rellenar el listview
        getListaArchivos();
        //esto lo ordena alfabeticamente
        Collections.sort(listadoArchivos, new Comparator<Archivo>() {
            public int compare(Archivo a, Archivo b) {
                return a.getTitulo().compareTo(b.getTitulo());
            }
        });

        //pasamos el listado al adaptador de la listview
        final AdaptadorArchivo archivos = new AdaptadorArchivo(getActivity(), listadoArchivos);
        listViewArchivos.setAdapter(archivos);

        //al clicar un item de la lista te envio a la activity que tiene los controles del mediaplayer
        listViewArchivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                Intent intent = new Intent(getContext(), PlayerActivity.class);
                //obtengo y envio a la otra activity el nombre del archivo escogido 
                String nombre = listadoArchivos.get(position).getTitulo();
                intent.putExtra("titulo", nombre);
                startActivity(intent);
            }
        });

        return view;
    }

    //este metodo lee los archivos que hay en el directorio y guarda los nombres en el arraylist
    public void getListaArchivos() {

        File rutaAudio = new File(Environment.getExternalStorageDirectory() + "/RecordedAudio/");
        File[] archivosAudio = rutaAudio.listFiles();

        for (int i = 0; i < archivosAudio.length; i++) {
            File file = archivosAudio[i];
            listadoArchivos.add(new Archivo(file.getName()));
        }

        File rutaVideos = new File(Environment.getExternalStorageDirectory() + "/RecordedVideo/");
        File[] archivosVideo = rutaVideos.listFiles();

        for (int i = 0; i < archivosVideo.length; i++) {
            File file = archivosVideo[i];
            listadoArchivos.add(new Archivo(file.getName()));
        }
    }

}
