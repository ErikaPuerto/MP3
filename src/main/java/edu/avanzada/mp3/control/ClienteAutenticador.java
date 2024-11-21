package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.ClienteDAO;
import edu.avanzada.mp3.modelo.ClienteVO;
import edu.avanzada.mp3.modelo.IMensajeria;
import edu.avanzada.mp3.vista.ControladorVentana;

import java.io.*;
import java.sql.SQLException;

/**
 * Clase para autenticar clientes y gestionar el cierre de sesión.
 */
public class ClienteAutenticador {

    private final ClienteDAO clienteDAO;
    private final ControladorVentana mensajes;
    private final IMensajeria mensajeria;

    /**
     * Constructor de {@code ClienteAutenticador}.
     *
     * @param mensajes   el controlador de mensajes
     * @param mensajeria la interfaz de mensajería
     */
    public ClienteAutenticador(ControladorVentana mensajes,IMensajeria mensajeria) {
        this.mensajeria = mensajeria;
        this.clienteDAO = new ClienteDAO(mensajeria);
        this.mensajes = mensajes;
    }

    /**
     * Autentica a un cliente mediante sus datos de entrada.
     *
     * @param entrada flujo de entrada de objetos para recibir datos
     * @param salida  flujo de salida de objetos para enviar resultados
     * @return el objeto {@link ClienteVO} si la autenticación es exitosa; {@code null} en caso contrario
     * @throws IOException            si ocurre un error de entrada/salida
     * @throws ClassNotFoundException si no se puede leer la clase del objeto
     * @throws SQLException           si ocurre un error en la base de datos
     */
    public ClienteVO autenticarCliente(ObjectInputStream entrada, ObjectOutputStream salida) throws IOException, ClassNotFoundException, SQLException {
        mensajes.mostrarMensajeSystem("Recibiendo datos de autenticacion del cliente...");
        String usuario = (String) entrada.readObject();
        String contrasena = (String) entrada.readObject();
        mensajes.mostrarMensajeSystem("Usuario recibido: " + usuario);

        ClienteVO clienteVO = clienteDAO.autenticarCliente(usuario, contrasena);
        if (clienteVO == null) {
            mensajes.mostrarMensajeSystem("Autenticacion fallida: Usuario o contraseña incorrectos.");
            salida.writeObject("Error: Usuario o contraseña incorrectos");
            salida.flush();
        } else {
            mensajes.mostrarMensajeSystem("Autenticacion exitosa para el usuario: " + usuario);
            salida.writeObject("Autenticacion exitosa");
            salida.flush();
        }
        return clienteVO;
    }

     /**
     * Finaliza la sesión de un cliente, actualizando la deuda.
     *
     * @param salida      flujo de salida de objetos para enviar resultados
     * @param clienteVO   el objeto {@link ClienteVO} del cliente autenticado
     * @param costoSesion el costo de la sesión a agregar a la deuda
     * @throws IOException  si ocurre un error de entrada/salida
     * @throws SQLException si ocurre un error en la base de datos
     */
    public void finalizarSesion(ObjectOutputStream salida, ClienteVO clienteVO, int costoSesion) throws IOException, SQLException {
        if (clienteVO != null) {
            int nuevaDeuda = clienteVO.getDeuda() + costoSesion;
            clienteVO.setDeuda(nuevaDeuda);

            clienteDAO.actualizarEstadoPago(clienteVO.getUsuario(), nuevaDeuda);

            salida.writeObject(String.format("Total a pagar: $f", nuevaDeuda));
            salida.writeObject("Sesion terminada");
            salida.flush();

            mensajes.mostrarMensajeSystem("Sesion finalizada para el usuario: " + clienteVO.getUsuario());
            mensajes.mostrarMensaje("Costo de la sesion: $" + costoSesion);
            mensajes.mostrarMensaje("Deuda total: $" + nuevaDeuda);
        } else {
            salida.writeObject("Error: No hay sesión activa");
            salida.writeObject("Sesion terminada");
            salida.flush();
        }
    }
}