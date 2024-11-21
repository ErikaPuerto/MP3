package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.Conexion;
import edu.avanzada.mp3.modelo.IMensajeria;
import edu.avanzada.mp3.vista.ControladorVentana;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Clase que representa el servidor de la aplicación de reproducción de música.
 * Se encarga de iniciar el servidor, aceptar conexiones de clientes y manejarlas mediante hilos.
 */
public class Servidor {

    private ServerSocket serverSocket;
    private final IMensajeria mensajeria;
    private ControladorVentana mensajes;

    /**
     * Constructor de Servidor.
     * Inicializa la ventana de mensajes y la conexión a la base de datos.
     */
    public Servidor() {
        this.mensajes = new ControladorVentana();
        this.mensajeria = new AdaptadorMensajeria(mensajes);
        Conexion.inicializar(mensajeria);
    }
    
    /**
     * Inicia el servidor en el puerto especificado.
     * Acepta conexiones de clientes y crea hilos para atenderlas.
     * 
     * @param puerto El número del puerto en el que se iniciará el servidor.
     */
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

    /**
     * Punto de entrada principal para iniciar el servidor.
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.iniciar(8080); // Iniciar en puerto 8080
    }
}
