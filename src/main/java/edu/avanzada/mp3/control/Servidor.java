package edu.avanzada.mp3.control;

import edu.avanzada.mp3.vista.ControladorVentana;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private ServerSocket serverSocket;
    private ControladorVentana mensajes;

    public Servidor(){this.mensajes = new ControladorVentana();}
    
    public void iniciar(int puerto) {
        try (ServerSocket serverSocket = new ServerSocket(puerto)){
            mensajes.mostrarMensajeSystem("Servidor iniciado en el puerto: " + puerto);

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                // Crear un hilo para atender al cliente
                ControladorCliente hilo = new ControladorCliente(clienteSocket);
                hilo.start();  // Arrancar el hilo de atención para este cliente
            }
        } catch (IOException e) {
            mensajes.mostrarMensajeSystem("Error al iniciar el servidor o aceptar una conexión: " + e.getMessage());
            e.printStackTrace();
        }finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    mensajes.mostrarMensajeSystem("Error al cerrar el servidor: " + ex.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.iniciar(8080); // Iniciar en puerto 8080
    }
}
