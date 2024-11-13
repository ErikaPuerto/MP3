package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.ClienteVO;
import edu.avanzada.mp3.vista.ControladorVentana;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControladorCliente extends Thread {

    private final Socket socket;
    private final ControladorVentana mensajes;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private final ClienteAutenticador autenticador;
    private final GestorCanciones gestorCanciones;
    private ClienteVO clienteVO;

    public ControladorCliente(Socket socket) {
        this.socket = socket;
        this.mensajes = new ControladorVentana();
        this.autenticador = new ClienteAutenticador(mensajes);
        this.gestorCanciones = new GestorCanciones(mensajes);
    }

    @Override
    public void run() {
        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());

            // Autenticación del cliente
            clienteVO = autenticador.autenticarCliente(entrada, salida);

            // Si autenticación exitosa, enviar lista de canciones y manejar descargas
            if (clienteVO != null) {
                mensajes.mostrarMensajeSystem("Autenticación exitosa. Mostrando menú de canciones...");
                gestorCanciones.enviarCancionesDisponibles(salida); // Mostrar la lista de canciones
                double costoSesion = gestorCanciones.manejarDescargas(entrada, salida, clienteVO);
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
