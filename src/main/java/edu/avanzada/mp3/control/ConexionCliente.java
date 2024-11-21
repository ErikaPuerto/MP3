package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.ClienteDAO;
import edu.avanzada.mp3.modelo.Conexion;
import edu.avanzada.mp3.modelo.IMensajeria;
import edu.avanzada.mp3.vista.ControladorVentana;
import edu.avanzada.mp3.vista.VistaLogin;
import edu.avanzada.mp3.vista.VistaPagar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestiona la conexión del cliente con el servidor y la autenticación del usuario.
 */
public class ConexionCliente implements ActionListener {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String nombreUsuario;
    private ControladorVentana mensajes;
    private ClienteDAO clienteDAO;
    private final VistaLogin vistaL;
    private VistaPagar recibo;
    private final IMensajeria mensajeria;

    /**
     * Constructor de {@code ConexionCliente}.
     *
     * @param mensajes controlador para mostrar mensajes en la interfaz
     */
    public ConexionCliente(ControladorVentana mensajes) {
        this.mensajes = mensajes;
        this.mensajeria = new AdaptadorMensajeria(mensajes);
        Conexion.inicializar(mensajeria);
        this.clienteDAO = new ClienteDAO(mensajeria);
        vistaL = new VistaLogin();
        vistaL.jButton1.addActionListener(this);
    }

    /**
     * Inicia la conexión al servidor verificando la IP y el puerto configurados.
     */
    public void iniciarConexion() {
    // Leer IP configurada desde el archivo properties
    String ipConfigurada = obtenerIpDesdeProperties();
    int puerto = obtenerPuertoDesdeProperties();

    if (ipConfigurada == null || ipConfigurada.isEmpty()) {
        mensajes.mostrarError("Error al cargar la IP desde el archivo properties.");
        System.exit(0); // Cerrar el programa
    }

    // Solicitar IP del usuario
    String ipServidor = mensajes.mostrarInputDialog("Introducir IP_SERVER:", "localhost");

    // Verificar si la IP ingresada coincide con la configurada
    if (!ipServidor.equals(ipConfigurada)) {
        mensajes.mostrarError("La IP ingresada no coincide con la IP configurada en el archivo properties. Programa cerrado.");
        System.exit(0); 
    }

    if (puerto <= 0) {
        mensajes.mostrarError("Error al cargar el puerto desde el archivo properties. Verifica que sea un número valido.");
        System.exit(0); 
    }

    mensajes.mostrarMensajeSystem("Conectando a IP: " + ipServidor + ", Puerto: " + puerto);
    conectarAlServidor(ipServidor, puerto);
}


/**
     * Obtiene la IP desde el archivo de propiedades.
     *
     * @return la IP configurada
     */
private String obtenerIpDesdeProperties() {
    Properties properties = new Properties();
    try (FileInputStream fis = new FileInputStream("src/main/resources/servidor.properties")) {
        properties.load(fis);
        String ip = properties.getProperty("ip");
        mensajes.mostrarMensajeSystem("IP cargada desde el archivo properties: " + ip);
        return ip;
    } catch (IOException e) {
        e.printStackTrace();
        mensajes.mostrarError("Error al leer el archivo properties: " + e.getMessage());
        return null;
    }
}

/**
     * Obtiene el puerto desde el archivo de propiedades.
     *
     * @return el puerto configurado o -1 si hay un error
     */
private int obtenerPuertoDesdeProperties() {
    Properties properties = new Properties();
    try (FileInputStream fis = new FileInputStream("src/main/resources/servidor.properties")) {
        properties.load(fis);
        String puertoStr = properties.getProperty("puerto");
        mensajes.mostrarMensajeSystem("Puerto cargado desde el archivo properties: " + puertoStr);
        return Integer.parseInt(puertoStr);
    } catch (IOException e) {
        e.printStackTrace();
        mensajes.mostrarError("Error al leer el archivo properties: " + e.getMessage());
    } catch (NumberFormatException e) {
        e.printStackTrace();
        mensajes.mostrarError("El puerto en el archivo properties no es un número valido.");
    }
    return -1; // Retorna un valor inválido si ocurre un error
}

    /**
     * Conecta el cliente al servidor en el host y puerto especificados.
     *
     * @param host  la dirección IP del servidor
     * @param puerto el puerto de conexión
     */
    private void conectarAlServidor(String host, int puerto) {
        try {
            socket = new Socket(host, puerto);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            mensajes.mostrarMensajeSystem("Conexion establecida con el servidor en " + host + ":" + puerto);
        } catch (IOException e) {
            mensajes.mostrarError("Error al conectar al servidor: " + e.getMessage());
            cerrarConexion();
        }
    }

     /**
     * Autentica al usuario enviando sus credenciales al servidor.
     *
     * @throws ClassNotFoundException si ocurre un error al recibir la respuesta
     */
    private void autenticarUsuario() throws ClassNotFoundException {
        try {
            mensajes.mostrarMensajeSystem("Enviando usuario y contraseña al servidor...");
            out.writeObject(vistaL.usuario.getText()); // Enviar usuario
            out.writeObject(vistaL.contraseña.getText()); // Enviar contraseña
            out.flush();

            mensajes.mostrarMensajeSystem("Esperando respuesta del servidor...");
            String respuesta = (String) in.readObject();

            if ("Autenticacion exitosa".equals(respuesta)) {
                manejarAutenticacionExitosa();
            } else {
                mensajes.mostrarError("Error de autenticacion: " + respuesta);
                cerrarConexion();
                System.exit(0);
            }
        } catch (IOException e) {
            mensajes.mostrarError("Error al comunicarse con el servidor: " + e.getMessage());
            cerrarConexion();
            System.exit(0);
        }
    }

    /**
     * Maneja los pasos a seguir tras una autenticación exitosa.
     *
     * @throws ClassNotFoundException si ocurre un error al verificar la deuda del cliente
     */
    private void manejarAutenticacionExitosa() throws ClassNotFoundException {
        try {
            int deuda = clienteDAO.verificarDeuda(vistaL.usuario.getText());
            if (deuda == 0) {
                new MenuCliente(socket, out, in, vistaL.usuario.getText(), mensajes, clienteDAO).mostrarCanciones();
                vistaL.dispose();
            } else {
                recibo = new VistaPagar();
                recibo.pagar.addActionListener(this);
                String text = "Cliente: " + vistaL.usuario.getText() + "<p>"
                        + "<p>Valor deuda: " + deuda
                        + "<p>Pague en menos de una semana su deuda.";
                recibo.recibo.setText("<html>" + text + "<html>");
            }
        } catch (SQLException e) {
            mensajes.mostrarError("Error al verificar la deuda del cliente: " + e.getMessage());
        }
    }

    /**
     * Cierra la conexión de red y libera los recursos asociados.
     */
    public void cerrarConexion() {
        try {
            if (out != null) {
                out.writeObject("logout");
                out.flush();
            }
        } catch (IOException e) {
            mensajes.mostrarMensajeSystem("No se pudo notificar al servidor el cierre de sesion.");
        }

        try {
            if (in != null) in.close();
        } catch (IOException e) {
            mensajes.mostrarMensajeSystem("Error al cerrar el flujo de entrada: " + e.getMessage());
        }

        try {
            if (out != null) out.close();
        } catch (IOException e) {
            mensajes.mostrarMensajeSystem("Error al cerrar el flujo de salida: " + e.getMessage());
        }

        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            mensajes.mostrarMensajeSystem("Error al cerrar el socket: " + e.getMessage());
        }

        mensajes.mostrarMensajeSystem("Conexion de cliente cerrada correctamente.");
    }

    /**
     * Maneja los eventos de acción de los componentes de la interfaz gráfica.
     *
     * @param e el evento de acción
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if ("INGRESAR".equals(e.getActionCommand())) {
            try {
                vistaL.setVisible(false);
                autenticarUsuario();
            } catch (ClassNotFoundException ex) {
                mensajes.mostrarError("Error inesperado durante la autenticacion: " + ex.getMessage());
                cerrarConexion();
                System.exit(0);
            }
        }

        if ("Okey".equals(e.getActionCommand())) {
            recibo.dispose();
            cerrarConexion();
            System.exit(0);
        }
    }
}