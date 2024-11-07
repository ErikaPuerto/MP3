package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.CancionDAO;
import edu.avanzada.mp3.modelo.ClienteDAO;
import edu.avanzada.mp3.modelo.Cancion;
import edu.avanzada.mp3.modelo.ClienteVO;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControladorCliente extends Thread {
    private Socket socket;
    private ClienteVO cliente;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private ClienteDAO clienteDAO;
    private CancionDAO cancionDAO;
    private double costoSesion = 0.0;

    public ControladorCliente(Socket socket) {
        this.socket = socket;
        this.clienteDAO = new ClienteDAO();
        this.cancionDAO = new CancionDAO();
    }

    @Override
    public void run() {
        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());

            autenticarCliente();
            enviarCancionesDisponibles();
            manejarDescargas();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControladorCliente.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            cerrarConexion();
        }
    }

    private void autenticarCliente() throws IOException, SQLException, ClassNotFoundException {
        // Lee usuario y contraseña, autentica y envía respuesta
        String usuario = (String) entrada.readObject();
        String contrasena = (String) entrada.readObject();
        cliente = clienteDAO.autenticarCliente(usuario, contrasena);
        if (cliente == null) {
            salida.writeObject("Error: Usuario o contraseña incorrectos");
            cerrarConexion();
        } else {
            salida.writeObject("Autenticación exitosa");
        }
    }

    private void enviarCancionesDisponibles() throws IOException, SQLException {
        List<Cancion> canciones = cancionDAO.obtenerCancionesDisponibles();
        salida.writeObject(canciones);
    }

    private void manejarDescargas() throws IOException, SQLException, ClassNotFoundException {
        // Maneja la selección y descarga de canciones por el cliente
        while (true) {
            String mensaje = (String) entrada.readObject();
            
            if (mensaje.equals("logout")) {
                finalizarSesion();
                break;
            } else if (mensaje.startsWith("descargar")) {
                String nombreCancion = mensaje.split(" ")[1];
                enviarCancion(nombreCancion);
                costoSesion += 15000;
            }
        }
    }

    private void enviarCancion(String nombreCancion) {
        File archivoCancion = new File("canciones/" + nombreCancion + ".mp3"); // Supongamos que la canción está en formato MP3 y en esta carpeta

        try (FileInputStream fis = new FileInputStream(archivoCancion);
             BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream())) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            salida.writeObject("iniciando_descarga"); // Notificamos al cliente
            while ((bytesRead = fis.read(buffer)) > 0) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();  // Enviamos todo el archivo
            salida.writeObject("descarga_completada"); // Informamos al cliente que la descarga ha terminado

        } catch (IOException e) {
            try {
                salida.writeObject("error_descarga"); // En caso de error
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }


    private void finalizarSesion() throws IOException, SQLException {
        salida.writeObject("Total a pagar: $" + costoSesion);
        clienteDAO.actualizarEstadoPago(cliente.getId(), cliente.getEstadoPago() + costoSesion);
        salida.writeObject("Sesion terminada");
    }

    private void cerrarConexion() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
