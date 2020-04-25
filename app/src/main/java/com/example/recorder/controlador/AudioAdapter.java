package com.example.recorder.controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.recorder.R;

import java.util.ArrayList;

//esta clase es el adaptador para rellenar el listview con el array de audios

public class AudioAdapter extends BaseAdapter {

    //se instancian dos cosas, un arraylist para los audios y un layout
    private ArrayList<Audio> listadoAudios;
    private LayoutInflater audioInfo;

    //constructor
    public AudioAdapter(Context context, ArrayList<Audio> audios) {
        listadoAudios = audios;
        audioInfo = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listadoAudios.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //se crea un archivo xml que es simplemente un textview con detalles del audio y se le pasa al layout
        LinearLayout layout = (LinearLayout) audioInfo.inflate(R.layout.audio, parent, false);
        //en este textview se pondra el titulo del audio
        TextView audioTitle = layout.findViewById(R.id.audio_title);
        //se crea objeto audio con el objeto de la lista
        Audio audio = listadoAudios.get(position);
        //en el textview se pone el nombre del audio encontrado en la lista
        audioTitle.setText(audio.getTitulo());
        layout.setTag(position);
        return layout;
    }
}
