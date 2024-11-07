package edu.avanzada.mp3.modelo;

import edu.avanzada.mp3.control.ControladorServidor;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class HiloAtencionCliente extends Thread {
    private Socket socket;
    private ControladorServidor controladorServidor;
    private PrintWriter out;
    private BufferedReader in;

    public HiloAtencionCliente(Socket socket, ControladorServidor controladorServidor) {
        this.socket = socket;
        this.controladorServidor = controladorServidor;
    }

    @Override
    public void run() {
        try {
            // Establecer flujos de entrada y salida
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Autenticación del cliente
            String usuario = in.readLine();
            String contrasena = in.readLine();

            // Autenticamos al cliente
            ClienteVO cliente = controladorServidor.autenticarCliente(usuario, contrasena);
            if (cliente != null) {
                out.println("Autenticación exitosa.");

                // Obtener canciones disponibles
                List<Cancion> canciones = controladorServidor.obtenerCancionesDisponibles();
                for (Cancion cancion : canciones) {
                    out.println(cancion.getNombre() + " - " + cancion.getArtista());
                }

                // Procesar la selección de canciones
                String cancionSeleccionada = in.readLine();
                out.println("Descargando " + cancionSeleccionada);

                // Actualizamos el pago del cliente
                controladorServidor.actualizarEstadoPago(cliente.getId(), cliente.getEstadoPago() + 15000);

                // Enviar mensaje al cliente
                out.println("Descarga completada. Pago total: " + (cliente.getEstadoPago() + 15000));

            } else {
                out.println("Autenticación fallida.");
            }

        } catch (IOException | SQLException e) {
            System.out.println("Error al atender al cliente: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar el socket: " + e.getMessage());
            }
        }
    }
}
