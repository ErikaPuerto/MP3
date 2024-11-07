package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.HiloAtencionCliente;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private ServerSocket serverSocket;
    private ControladorServidor controladorServidor;

    public Servidor() {
        controladorServidor = new ControladorServidor();  // Inicializamos el controlador del servidor
    }

    public void iniciar(int puerto) {
        try {
            serverSocket = new ServerSocket(puerto);
            System.out.println("Servidor iniciado en el puerto: " + puerto);

            while (true) {
                // Acepta la conexión de un cliente
                Socket clienteSocket = serverSocket.accept();
                
                // Se crea un nuevo hilo de atención para este cliente
                new HiloAtencionCliente(clienteSocket, controladorServidor).start();
            }
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.iniciar(8080); // Iniciar en el puerto 8080
    }
}
