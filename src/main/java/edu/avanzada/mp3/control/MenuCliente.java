package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.ClienteDAO;
import edu.avanzada.mp3.vista.ControladorVentana;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Scanner;

public class MenuCliente {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String nombreUsuario;
    private ControladorVentana mensajes;
    private ClienteDAO clienteDAO;

    public MenuCliente(Socket socket, ObjectOutputStream out, ObjectInputStream in, String nombreUsuario, ControladorVentana mensajes, ClienteDAO clienteDAO) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.nombreUsuario = nombreUsuario;
        this.mensajes = mensajes;
        this.clienteDAO = clienteDAO;
    }

    public void mostrarMenu() {
        try {
            int numeroCanciones = in.readInt();
            mensajes.mostrarMensajeSystem("Número de canciones recibidas: " + numeroCanciones);

            if (numeroCanciones == 0) {
                mensajes.mostrarMensaje("No hay canciones disponibles.");
            } else {
                for (int i = 0; i < numeroCanciones; i++) {
                    String cancion = in.readUTF();
                    mensajes.mostrarMensajeSystem("Canción " + (i + 1) + ": " + cancion);
                }
            }

            // Leer el mensaje final "fin_lista"
            String finLista = in.readUTF();
            if ("fin_lista".equals(finLista)) {
                mensajes.mostrarMensajeSystem("Lista de canciones recibida correctamente.");
            } else {
                mensajes.mostrarError("Error: no se recibió el final esperado de la lista.");
            }

            // Mostrar el menú para elegir la opción
            mostrarOpciones(numeroCanciones);
        } catch (IOException e) {
            mensajes.mostrarError("Error al recibir la lista de canciones: " + e.getMessage());
        }
    }

    private void mostrarOpciones(int numeroCanciones) {
        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 3) {
            mensajes.mostrarMensajeSystem("Opciones:");
            mensajes.mostrarMensajeSystem("1. Descargar una canción");
            mensajes.mostrarMensajeSystem("2. Actualizar lista de canciones");
            mensajes.mostrarMensajeSystem("3. Salir");

            System.out.print("Seleccione una opción: ");
            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                switch (opcion) {
                    case 1:
                        if (numeroCanciones > 0) {
                            try {
                                if (clienteDAO.verificarDeuda(nombreUsuario)) {
                                    seleccionarYDescargarCancion(numeroCanciones);
                                } else {
                                    mensajes.mostrarMensaje("Tienes una deuda pendiente. No puedes descargar canciones.");
                                    mensajes.mostrarMensaje("Plazo Maximo: 1 (una) semana");
                                    cerrarConexion();
                                }
                            } catch (SQLException e) {
                                mensajes.mostrarMensaje("Error al verificar la deuda: " + e.getMessage());
                            }
                        } else {
                            mensajes.mostrarMensaje("No hay canciones disponibles.");
                        }
                        break;

                    case 2:
                        solicitarActualizarCanciones();
                        break;

                    case 3:
                        mensajes.mostrarMensaje("Cerrando sesión...");
                        cerrarConexion();
                        break;

                    default:
                        mensajes.mostrarMensaje("Opción inválida.");
                }
            } else {
                mensajes.mostrarMensaje("Por favor ingrese un número válido.");
                scanner.next(); // Limpiar el buffer
            }
        }
    }

    private void seleccionarYDescargarCancion(int numeroCanciones) {
        Scanner scanner = new Scanner(System.in);
        int opcion = -1;
        while (opcion < 1 || opcion > numeroCanciones) {
            mensajes.mostrarMensajeSystem("Ingrese el número de la canción que desea descargar (1-" + numeroCanciones + "): ");
            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                if (opcion < 1 || opcion > numeroCanciones) {
                    mensajes.mostrarMensaje("Opción inválida, por favor elija un número entre 1 y " + numeroCanciones);
                }
            } else {
                mensajes.mostrarMensaje("Por favor ingrese un número válido.");
                scanner.next();
            }
        }

        try {
            // Enviar la opción al servidor
            out.writeObject(String.valueOf(opcion));

            // Recibir el nombre de la canción o mensajes de error
            String respuesta = (String) in.readObject();

            if (respuesta.startsWith("error_")) {
                mensajes.mostrarMensajeSystem("Error: " + respuesta.substring(6).replace('_', ' '));
                return;
            }

            // Es un nombre de canción válido, proceder con la descarga
            String nombreCancion = respuesta;
            String nombreArchivo = nombreCancion + "_" + this.nombreUsuario + ".mp3";
            String rutaDestino = "descargas/" + nombreArchivo;

            // Asegurarse de que el directorio de descargas existe
            new File("descargas").mkdirs();

            // Preparar para recibir el archivo
            try (FileOutputStream fos = new FileOutputStream(rutaDestino);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                byte[] buffer = new byte[4096];
                int bytesLeidos;

                mensajes.mostrarMensajeSystem("Iniciando descarga de " + nombreCancion);
                mensajes.mostrarMensajeSystem("Se guardará como: " + nombreArchivo);

                // Recibir el archivo sin conocer el tamaño previamente
                while ((bytesLeidos = in.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesLeidos);
                }

                mensajes.mostrarMensajeSystem("\nDescarga completada: " + rutaDestino);
            }

            // Esperar el mensaje de confirmación
            String confirmacion = (String) in.readObject();
            if ("descarga_completada".equals(confirmacion)) {
                mensajes.mostrarMensajeSystem("La descarga ha finalizado correctamente.");
            } else {
                mensajes.mostrarMensajeSystem("Error: No se recibió la confirmación esperada.");
            }

        } catch (EOFException e) {
            mensajes.mostrarMensaje("Error: Se alcanzó el final del flujo de datos inesperadamente.");
        } catch (IOException | ClassNotFoundException e) {
            mensajes.mostrarMensajeSystem("Error durante la descarga: " + e.getMessage());
        }
    }

    private void solicitarActualizarCanciones() {
        try {
            out.writeObject("actualizar");
            out.flush();
            mensajes.mostrarMensajeSystem("Solicitando actualización de la lista de canciones...");
            mostrarMenu();
        } catch (IOException e) {
            mensajes.mostrarError("Error al enviar solicitud de actualización: " + e.getMessage());
        }
    }

    // Método para cerrar la conexión de red y liberar recursos
    private void cerrarConexion() {
        try {
            out.writeObject("logout");
            out.flush();
            String costoTotal = (String) in.readObject();
            String mensajeFinal = (String) in.readObject();
            mensajes.mostrarMensajeSystem(costoTotal);
            mensajes.mostrarMensajeSystem(mensajeFinal);

            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            mensajes.mostrarMensajeSystem("Conexión de cliente cerrada.");
            System.exit(0);
        } catch (IOException | ClassNotFoundException e) {
            mensajes.mostrarMensajeSystem("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
