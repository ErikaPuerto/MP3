package edu.avanzada.mp3.modelo;

import java.io.Serializable;

/**
 * Clase que representa una canción en el sistema.
 * Contiene información básica sobre la canción, como su nombre, artista y ubicación del archivo.
 */
public class Cancion implements Serializable{
    private String nombre;
    private String artista;
    private String ubicacion;

    /**
     * Constructor de la clase Cancion.
     * 
     * @param nombre Nombre de la canción.
     * @param artista Artista de la canción.
     * @param ubicacion Ruta de ubicación del archivo de la canción.
     */
    public Cancion(String nombre, String artista, String ubicacion) {
        this.nombre = nombre;
        this.artista = artista;
        this.ubicacion = ubicacion;
    }
    
    /**
     * Metodos get de nombre, artista y ubicacion
     * @return nombre, artista y ubicacion
     */
    public String getNombre() { return nombre; }
    public String getArtista() { return artista; }
    public String getUbicacion(){ return ubicacion;}
}

