package edu.avanzada.mp3.modelo;

import java.io.Serializable;

public class Cancion implements Serializable{
    private String nombre;
    private String artista;
    private String ubicacion;


    public Cancion(String nombre, String artista, String ubicacion) {
        this.nombre = nombre;
        this.artista = artista;
        this.ubicacion = ubicacion;
    }

    public String getNombre() { return nombre; }
    public String getArtista() { return artista; }
    public String getUbicacion(){ return ubicacion;}
}

