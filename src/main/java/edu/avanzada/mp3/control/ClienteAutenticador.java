package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.ClienteDAO;
import edu.avanzada.mp3.modelo.ClienteVO;
import edu.avanzada.mp3.vista.ControladorVentana;

import java.io.*;
import java.sql.SQLException;

public class ClienteAutenticador {

    private final ClienteDAO clienteDAO;
    private final ControladorVentana mensajes;

    public ClienteAutenticador(ControladorVentana mensajes) {
        this.clienteDAO = new ClienteDAO();
        this.mensajes = mensajes;
    }

    public ClienteVO autenticarCliente(ObjectInputStream entrada, ObjectOutputStream salida) throws IOException, ClassNotFoundException, SQLException {
        mensajes.mostrarMensajeSystem("Recibiendo datos de autenticación del cliente...");
        String usuario = (String) entrada.readObject();
        String contrasena = (String) entrada.readObject();
        mensajes.mostrarMensajeSystem("Usuario recibido: " + usuario);

        ClienteVO clienteVO = clienteDAO.autenticarCliente(usuario, contrasena);
        if (clienteVO == null) {
            mensajes.mostrarMensajeSystem("Autenticación fallida: Usuario o contraseña incorrectos.");
            salida.writeObject("Error: Usuario o contraseña incorrectos");
            salida.flush();
        } else {
            mensajes.mostrarMensajeSystem("Autenticación exitosa para el usuario: " + usuario);
            salida.writeObject("Autenticación exitosa");
            salida.flush();
        }
        return clienteVO;
    }

    public void finalizarSesion(ObjectOutputStream salida, ClienteVO clienteVO, double costoSesion) throws IOException, SQLException {
        if (clienteVO != null) {
            double nuevaDeuda = clienteVO.getDeuda() + costoSesion;
            clienteVO.setDeuda(nuevaDeuda);

            clienteDAO.actualizarEstadoPago(clienteVO.getUsuario(), nuevaDeuda);

            salida.writeObject(String.format("Total a pagar: $%.2f", nuevaDeuda));
            salida.writeObject("Sesión terminada");
            salida.flush();

            mensajes.mostrarMensajeSystem("Sesión finalizada para el usuario: " + clienteVO.getUsuario());
            mensajes.mostrarMensaje("Costo de la sesión: $" + costoSesion);
            mensajes.mostrarMensaje("Deuda total: $" + nuevaDeuda);
        } else {
            salida.writeObject("Error: No hay sesión activa");
            salida.writeObject("Sesión terminada");
            salida.flush();
        }
    }
}
