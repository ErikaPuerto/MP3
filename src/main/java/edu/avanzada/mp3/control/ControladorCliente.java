package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.CancionDAO;
import edu.avanzada.mp3.modelo.ClienteDAO;
import edu.avanzada.mp3.modelo.Cancion;
import edu.avanzada.mp3.modelo.ClienteVO;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

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

            // Autenticación del cliente
            System.out.println("Iniciando autenticación del cliente...");
            autenticarCliente();

            // Enviar lista de canciones si autenticación exitosa
            System.out.println("Enviando lista de canciones disponibles...");
            enviarCancionesDisponibles();

            // Manejo de descargas
            System.out.println("Iniciando manejo de descargas...");
            manejarDescargas();

        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            cerrarConexion();
        }
    }

    private void autenticarCliente() throws IOException, SQLException, ClassNotFoundException {
        // Lee usuario y contraseña, autentica y envía respuesta
        System.out.println("Recibiendo datos de autenticación del cliente...");
        String usuario = (String) entrada.readObject();
        String contrasena = (String) entrada.readObject();
        System.out.println("Usuario recibido: " + usuario); // Mensaje de depuración

        // Intentar autenticar al cliente
        cliente = clienteDAO.autenticarCliente(usuario, contrasena);
        if (cliente == null) {
            System.out.println("Autenticación fallida: Usuario o contraseña incorrectos.");
            salida.writeObject("Error: Usuario o contraseña incorrectos");
            salida.flush();
            cerrarConexion();
        } else {
            System.out.println("Autenticación exitosa para el usuario: " + usuario);
            salida.writeObject("Autenticación exitosa");
            salida.flush();
        }
    }

    private void enviarCancionesDisponibles() throws IOException, SQLException {
        List<Cancion> canciones = cancionDAO.obtenerCancionesDisponibles();
        int numeroCanciones = canciones.size();
        salida.writeInt(numeroCanciones); // Enviar el número de canciones al cliente
        salida.flush(); // Enviar inmediatamente

        System.out.println("Enviando " + numeroCanciones + " canciones al cliente.");
        for (Cancion cancion : canciones) {
            salida.writeUTF(cancion.getNombre()); // Enviar el nombre de cada canción
            System.out.println("Canción enviada: " + cancion.getNombre());
        }
        
        // Enviar mensaje de finalización de envío
        salida.writeUTF("fin_lista");
        salida.flush();
        System.out.println("Final de lista enviado al cliente.");
        
        if (numeroCanciones == 0) {
            System.out.println("No hay canciones disponibles para enviar al cliente.");
            }
    }



    private void manejarDescargas() {
    try {
        while (true) {
            String mensaje = (String) entrada.readObject(); // Lee mensaje del cliente
            if ("logout".equals(mensaje)) {
                System.out.println("Cliente solicitó logout. Finalizando sesión.");
                finalizarSesion();
                break;
            } else if ("actualizar".equals(mensaje)) {
                System.out.println("Cliente solicitó actualización de la lista de canciones.");
                enviarCancionesDisponibles(); // Enviar la lista de canciones al cliente
            } else if (mensaje.startsWith("descargar")) {
                String nombreCancion = mensaje.split(" ")[1];
                System.out.println("Cliente solicitó descargar: " + nombreCancion);
                enviarCancion(nombreCancion);
                costoSesion += 15000; // Aumentar el costo de la sesión
            }
        }
    } catch (IOException | ClassNotFoundException | SQLException e) {
        e.printStackTrace();
    }
}




    private void enviarCancion(String nombreCancion) {
        File archivoCancion = new File("canciones/" + nombreCancion + ".mp3"); // Suponemos la ubicación de canciones
        try (FileInputStream fis = new FileInputStream(archivoCancion);
             BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream())) {
             
            salida.writeObject("iniciando_descarga"); // Notificación al cliente
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) > 0) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush(); // Enviar todo el archivo
            salida.writeObject("descarga_completada"); // Informar al cliente que la descarga ha terminado
            System.out.println("Descarga de " + nombreCancion + " completada.");

        } catch (IOException e) {
            try {
                salida.writeObject("error_descarga"); // Enviar error al cliente
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void finalizarSesion() throws IOException, SQLException {
        salida.writeObject("Total a pagar: $" + costoSesion);
        salida.writeObject("Sesion terminada");
        System.out.println("Sesión finalizada. Total a pagar: $" + costoSesion);
    }

    private void cerrarConexion() {
        try {
            if (salida != null) salida.close();
            if (entrada != null) entrada.close();
            if (socket != null) socket.close();
            System.out.println("Conexión del cliente cerrada.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
