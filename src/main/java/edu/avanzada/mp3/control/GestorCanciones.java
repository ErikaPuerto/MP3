package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.Cancion;
import edu.avanzada.mp3.modelo.CancionDAO;
import edu.avanzada.mp3.modelo.ClienteVO;
import edu.avanzada.mp3.modelo.IMensajeria;
import edu.avanzada.mp3.vista.ControladorVentana;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase {@code GestorCanciones} que gestiona las operaciones relacionadas con las canciones disponibles,
 * incluyendo su envío al cliente y el manejo de las descargas solicitadas.
 */
public class GestorCanciones {

    private final CancionDAO cancionDAO;
    private final ControladorVentana mensajes;
    private final IMensajeria mensajeria;

    /**
     * Constructor de {@code GestorCanciones}.
     * Inicializa el gestor de canciones con el controlador de mensajes y el sistema de mensajería.
     *
     * @param mensajes   el controlador de ventana para mostrar mensajes
     * @param mensajeria el adaptador de mensajería
     */
    public GestorCanciones(ControladorVentana mensajes, IMensajeria mensajeria) {
        this.mensajeria = mensajeria;
        this.cancionDAO = new CancionDAO(mensajeria);
        this.mensajes = mensajes;
    }

    /**
     * Envía la lista de canciones disponibles al cliente a través de la conexión de salida.
     *
     * @param salida el {@code ObjectOutputStream} utilizado para enviar datos al cliente
     * @throws IOException   si ocurre un error durante el envío de los datos
     * @throws SQLException si ocurre un error al obtener las canciones de la base de datos
     */
    public void enviarCancionesDisponibles(ObjectOutputStream salida) throws IOException, SQLException {
        List<Cancion> canciones = cancionDAO.obtenerCancionesDisponibles();
        int numeroCanciones = canciones.size();
        salida.writeInt(numeroCanciones);
        salida.flush();
        mensajes.mostrarMensajeSystem("Enviando " + numeroCanciones + " canciones al cliente.");
        for (Cancion cancion : canciones) {
            salida.writeUTF(cancion.getNombre());
            mensajes.mostrarMensajeSystem("Cancion enviada: " + cancion.getNombre());
        }
        salida.writeUTF("fin_lista");
        salida.flush();
    }

    /**
     * Maneja las solicitudes de descarga del cliente y gestiona la transferencia de las canciones.
     * También maneja las acciones de logout y actualización de la lista de canciones.
     *
     * @param entrada  el {@code ObjectInputStream} utilizado para recibir mensajes del cliente
     * @param salida   el {@code ObjectOutputStream} utilizado para enviar datos al cliente
     * @param clienteVO el objeto que representa al cliente autenticado
     * @return el costo total de las descargas realizadas durante la sesión
     */
    public int manejarDescargas(ObjectInputStream entrada, ObjectOutputStream salida, ClienteVO clienteVO) {
        int costoSesion = 0;
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

    /**
     * Procesa una descarga solicitada por el cliente. Envia la canción seleccionada al cliente y
     * actualiza el costo de la sesión.
     *
     * @param entrada     el {@code ObjectInputStream} utilizado para recibir datos del cliente
     * @param salida      el {@code ObjectOutputStream} utilizado para enviar datos al cliente
     * @param mensaje     el mensaje recibido del cliente con la opción de canción seleccionada
     * @param clienteVO   el objeto que representa al cliente autenticado
     * @return el costo de la canción descargada
     * @throws IOException   si ocurre un error durante la transferencia de archivos
     * @throws SQLException  si ocurre un error al obtener los datos de la canción desde la base de datos
     */
    private int procesarDescarga(ObjectInputStream entrada, ObjectOutputStream salida, String mensaje, ClienteVO clienteVO) throws IOException, SQLException {
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
                    return 0;
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

                return 15000; // Costo de descarga por canción
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
        return 0;
    }
}
