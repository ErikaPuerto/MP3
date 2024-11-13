package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.Cancion;
import edu.avanzada.mp3.modelo.CancionDAO;
import edu.avanzada.mp3.modelo.ClienteVO;
import edu.avanzada.mp3.vista.ControladorVentana;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class GestorCanciones {

    private final CancionDAO cancionDAO;
    private final ControladorVentana mensajes;

    public GestorCanciones(ControladorVentana mensajes) {
        this.cancionDAO = new CancionDAO();
        this.mensajes = mensajes;
    }

    public void enviarCancionesDisponibles(ObjectOutputStream salida) throws IOException, SQLException {
        List<Cancion> canciones = cancionDAO.obtenerCancionesDisponibles();
        int numeroCanciones = canciones.size();
        salida.writeInt(numeroCanciones);
        salida.flush();

        mensajes.mostrarMensajeSystem("Enviando " + numeroCanciones + " canciones al cliente.");
        for (Cancion cancion : canciones) {
            salida.writeUTF(cancion.getNombre());
            mensajes.mostrarMensajeSystem("Canción enviada: " + cancion.getNombre());
        }
        salida.writeUTF("fin_lista");
        salida.flush();
    }

    public double manejarDescargas(ObjectInputStream entrada, ObjectOutputStream salida, ClienteVO clienteVO) {
        double costoSesion = 0.0;
        try {
            while (true) {
                String mensaje = (String) entrada.readObject();
                mensajes.mostrarMensajeSystem("Mensaje recibido del cliente: " + mensaje);

                if ("logout".equals(mensaje)) {
                    mensajes.mostrarMensajeSystem("Cliente solicitó logout. Finalizando sesión.");
                    break;
                } else if ("actualizar".equals(mensaje)) {
                    enviarCancionesDisponibles(salida);
                } else {
                    costoSesion += procesarDescarga(entrada, salida, mensaje, clienteVO);
                }
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            mensajes.mostrarMensajeSystem("Error en la transferencia: " + e.getMessage());
            e.printStackTrace();
        }
        return costoSesion;
    }

    private double procesarDescarga(ObjectInputStream entrada, ObjectOutputStream salida, String mensaje, ClienteVO clienteVO) throws IOException, SQLException {
        try {
            int opcionCancion = Integer.parseInt(mensaje);
            List<Cancion> canciones = cancionDAO.obtenerCancionesDisponibles();

            if (opcionCancion > 0 && opcionCancion <= canciones.size()) {
                Cancion cancionSeleccionada = canciones.get(opcionCancion - 1);
                String nombreCancion = cancionSeleccionada.getNombre();
                String ubicacion = cancionSeleccionada.getUbicacion();

                salida.writeObject(nombreCancion);
                salida.flush();

                InputStream archivoCancion = getClass().getClassLoader().getResourceAsStream(ubicacion);

                if (archivoCancion == null) {
                    salida.writeObject("error_archivo_no_encontrado");
                    salida.flush();
                    mensajes.mostrarMensajeSystem("Error: El archivo no existe en la ubicación: " + ubicacion);
                    return 0.0;
                }

                try (BufferedInputStream bis = new BufferedInputStream(archivoCancion)) {
                    byte[] buffer = new byte[4096];
                    int bytesLeidos;
                    while ((bytesLeidos = bis.read(buffer)) != -1) {
                        salida.write(buffer, 0, bytesLeidos);
                    }
                    salida.flush();
                }
                mensajes.mostrarMensajeSystem("Archivo enviado exitosamente: " + nombreCancion + " al usuario: " + clienteVO.getUsuario());
                salida.writeObject("descarga_completada");
                salida.flush();

                return 15000.0; // Coste de descarga por canción
            } else {
                salida.writeObject("error_opcion_invalida");
                salida.flush();
                mensajes.mostrarError("Error: Opción de canción inválida.");
            }
        } catch (NumberFormatException e) {
            mensajes.mostrarMensajeSystem("Mensaje no reconocido: " + mensaje);
            salida.writeObject("error_formato_mensaje");
            salida.flush();
        }
        return 0.0;
    }
}
