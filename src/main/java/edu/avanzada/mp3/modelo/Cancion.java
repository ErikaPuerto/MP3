package edu.avanzada.mp3.modelo;

public class Cancion {
    private int id;
    private String nombre;
    private String artista;

    public Cancion(int id, String nombre, String artista) {
        this.id = id;
        this.nombre = nombre;
        this.artista = artista;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getArtista() { return artista; }
}

