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

public class AdaptadorArchivo extends BaseAdapter {

    //se instancian dos cosas, un arraylist para los audios y un layout
    private ArrayList<Archivo> listadoArchivos;
    private LayoutInflater archivoInfo;

    //constructor
    public AdaptadorArchivo(Context context, ArrayList<Archivo> archivos) {
        listadoArchivos = archivos;
        archivoInfo = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listadoArchivos.size();
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
        LinearLayout layout = (LinearLayout) archivoInfo.inflate(R.layout.archivo, parent, false);
        //en este textview se pondra el titulo del audio
        TextView tituloArchivo = layout.findViewById(R.id.tituloArchivo);
        //se crea objeto audio con el objeto de la lista
        Archivo archivo = listadoArchivos.get(position);
        //en el textview se pone el nombre del audio encontrado en la lista
        tituloArchivo.setText(archivo.getTitulo());
        layout.setTag(position);
        return layout;
    }
}
