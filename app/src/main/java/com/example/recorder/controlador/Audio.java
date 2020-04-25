package com.example.recorder.controlador;

//esta es la clase para obtener los audios y colocarlos en el listview

public class Audio {

    private String titulo;

    public Audio(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
