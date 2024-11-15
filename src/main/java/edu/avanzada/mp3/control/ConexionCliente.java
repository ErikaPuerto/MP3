package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.ClienteDAO;
import edu.avanzada.mp3.vista.ControladorVentana;
import edu.avanzada.mp3.vista.VistaLogin;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionCliente implements ActionListener {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String nombreUsuario;
    private ControladorVentana mensajes;
    private ClienteDAO clienteDAO;
    private final VistaLogin vistaL;

    public ConexionCliente(ControladorVentana mensajes) {
        this.mensajes = mensajes;
        this.clienteDAO = new ClienteDAO();
        vistaL = new VistaLogin();
        vistaL.jButton1.addActionListener(this);
        iniciarConexion();
    }

    // Método para iniciar la conexión
    public void iniciarConexion() {
        String ipServidor = JOptionPane.showInputDialog("Introducir IP_SERVER:", "localhost");
        int puerto = 8080;
        conectarAlServidor(ipServidor, puerto);
    }

    // Método para conectar al servidor
    private void conectarAlServidor(String host, int puerto) {
        try {
            socket = new Socket(host, puerto);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            mensajes.mostrarMensajeSystem("Conexión establecida con el servidor en " + host + ":" + puerto);
        } catch (IOException e) {
            mensajes.mostrarError("Error al conectar al servidor: " + e.getMessage());
            cerrarConexion();
        }
    }

    // Método para autenticar al usuario
    private void autenticarUsuario() throws ClassNotFoundException, SQLException {
        try {
            mensajes.mostrarMensajeSystem("Enviando usuario y contraseña al servidor...");
            //String usuario = mensajes.mostrarInputDialog("Ingrese su usuario: ", "usuario");
            //this.nombreUsuario = usuario;
            //String contrasena = mensajes.mostrarInputDialog("Ingrese su contraseña: ", "contraseña");
            mensajes.mostrarMensajeSystem("Esperando respuesta del servidor...");
            // Enviar usuario y contraseña al servidor usando writeObject
            out.writeObject(vistaL.usuario.getText()); // Enviar usuario
            out.writeObject(vistaL.contraseña.getText()); // Enviar contraseña
            out.flush(); // Asegurarse de enviar todos los datos
            String respuesta = (String) in.readObject();
            mensajes.mostrarMensajeSystem("Respuesta del servidor: " + respuesta);

            if ("Autenticación exitosa".equals(respuesta)) {
                new MenuCliente(socket, out, in, vistaL.usuario.getText(), mensajes, clienteDAO).mostrarCanciones();
                vistaL.dispose();

            } else {
                mensajes.mostrarError("Error de autenticación: " + respuesta);
                cerrarConexion();
            }
        } catch (IOException e) {
            mensajes.mostrarError("Error al comunicarse con el servidor: " + e.getMessage());
            cerrarConexion();
        }
    }

    // Método para cerrar la conexión de red y liberar recursos
    public void cerrarConexion() {
        try {
            out.writeObject("logout");
            out.flush();
            String costoTotal = (String) in.readObject();
            String mensajeFinal = (String) in.readObject();
            mensajes.mostrarMensajeSystem(costoTotal);
            mensajes.mostrarMensajeSystem(mensajeFinal);

            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
            mensajes.mostrarMensajeSystem("Conexión de cliente cerrada.");
            System.exit(0);
        } catch (IOException | ClassNotFoundException e) {
            mensajes.mostrarMensajeSystem("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("INGRESAR".equals(e.getActionCommand())) {
            try {
                autenticarUsuario();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ConexionCliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(ConexionCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}
