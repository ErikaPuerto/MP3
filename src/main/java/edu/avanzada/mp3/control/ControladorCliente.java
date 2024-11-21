package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.ClienteVO;
import edu.avanzada.mp3.modelo.Conexion;
import edu.avanzada.mp3.modelo.IMensajeria;
import edu.avanzada.mp3.vista.ControladorVentana;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase {@code ControladorCliente} que gestiona la conexión del cliente y el flujo de autenticación,
 * la visualización de canciones y la descarga de las mismas.
 */
public class ControladorCliente extends Thread {

    private final Socket socket;
    private final ControladorVentana mensajes;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private final IMensajeria mensajeria;
    private final ClienteAutenticador autenticador;
    private final GestorCanciones gestorCanciones;
    private ClienteVO clienteVO;

    /**
     * Constructor de {@code ControladorCliente}.
     *
     * @param socket el socket de comunicación con el cliente
     */
    public ControladorCliente(Socket socket) {
        this.socket = socket;
        this.mensajes = new ControladorVentana();
        this.mensajeria = new AdaptadorMensajeria(mensajes);
        Conexion.inicializar(mensajeria);
        this.autenticador = new ClienteAutenticador(mensajes, mensajeria);
        this.gestorCanciones = new GestorCanciones(mensajes, mensajeria);
    }

    /**
     * Método principal del hilo, gestiona la autenticación del cliente, la
     * visualización de canciones y el proceso de descarga.
     */
    @Override
    public void run() {
        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());

            // Autenticación del cliente
            clienteVO = autenticador.autenticarCliente(entrada, salida);

            // Si la autenticación es exitosa, enviar la lista de canciones y manejar descargas
            if (clienteVO != null) {
                mensajes.mostrarMensajeSystem("Autenticación exitosa. Mostrando menú de canciones...");
                gestorCanciones.enviarCancionesDisponibles(salida); // Mostrar la lista de canciones
                int costoSesion = gestorCanciones.manejarDescargas(entrada, salida, clienteVO);
                autenticador.finalizarSesion(salida, clienteVO, costoSesion);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(ControladorCliente.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            cerrarConexion();
        }
    }

    /**
     * Cierra la conexión del cliente y libera los recursos de entrada y salida.
     */
    private void cerrarConexion() {
        try {
            if (salida != null) salida.close();
            if (entrada != null) entrada.close();
            if (socket != null) socket.close();
            mensajes.mostrarMensajeSystem("Conexión del cliente cerrada.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
