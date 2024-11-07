package edu.avanzada.mp3.control;

public class Launcher {

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.iniciar(8080); // Puedes cambiar el puerto si es necesario
    }
}
