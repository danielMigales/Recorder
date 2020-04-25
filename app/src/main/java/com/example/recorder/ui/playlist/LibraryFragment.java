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
import com.example.recorder.controlador.Audio;
import com.example.recorder.controlador.AudioAdapter;
import com.example.recorder.ui.audio.PlayerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//este fragment es el que tiene el listado de audios encontrados en la carpeta que se ha creado

public class LibraryFragment extends Fragment {

    private LibraryViewModel libraryViewModel;

    //se declara un arraylist y un listview
    private ArrayList<Audio> listadoAudios;
    private ListView listviewAudios;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        libraryViewModel = ViewModelProviders.of(this).get(LibraryViewModel.class);
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        listviewAudios = view.findViewById(R.id.audioList);
        listadoAudios = new ArrayList<Audio>();

        //llamada al metodo que obtiene los datos para rellenar el listview
        getAudioList();
        //esto lo ordena alfabeticamente
        Collections.sort(listadoAudios, new Comparator<Audio>() {
            public int compare(Audio a, Audio b) {
                return a.getTitulo().compareTo(b.getTitulo());
            }
        });

        //pasamos el listado al adaptador de la listview
        final AudioAdapter audios = new AudioAdapter(getActivity(), listadoAudios);
        listviewAudios.setAdapter(audios);

        //al clicar un item de la lista te envio a la activity que tiene los controles del mediaplayer
        listviewAudios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                
                Intent intent = new Intent(getContext(), PlayerActivity.class);
                //obtengo y envio a la otra activity el nombre del archivo escogido 
                String nombre = listadoAudios.get(position).getTitulo();
                intent.putExtra("titulo", nombre);
                startActivity(intent);
            }
        });

        return view;
    }

    //este metodo lee los archivos que hay en el directorio y guarda los nombres en el arraylist
    public void getAudioList() {

        File archivo = new File(Environment.getExternalStorageDirectory() + "/RecordedAudio/");
        File[] files = archivo.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            listadoAudios.add(new Audio(file.getName()));
        }
    }
}
