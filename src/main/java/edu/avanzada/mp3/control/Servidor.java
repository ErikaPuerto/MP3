package edu.avanzada.mp3.control;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private ServerSocket serverSocket;

    public void iniciar(int puerto) {
        try {
            serverSocket = new ServerSocket(puerto);
            System.out.println("Servidor iniciado en el puerto: " + puerto);

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                // Crear un hilo para atender al cliente
                ControladorCliente hilo = new ControladorCliente(clienteSocket);
                hilo.start();  // Arrancar el hilo de atención para este cliente
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.iniciar(8080); // Iniciar en puerto 8080
    }
}
