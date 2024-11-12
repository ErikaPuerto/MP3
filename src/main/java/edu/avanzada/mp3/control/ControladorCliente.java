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
    private ClienteVO clienteVO;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private ClienteDAO clienteDAO;
    private CancionDAO cancionDAO;
    private Cliente cliente;
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
        System.out.println("Recibiendo datos de autenticación del cliente...");
        String usuario = (String) entrada.readObject();
        String contrasena = (String) entrada.readObject();
        System.out.println("Usuario recibido: " + usuario);

        clienteVO = clienteDAO.autenticarCliente(usuario, contrasena);
        if (clienteVO == null) {
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
        salida.writeInt(numeroCanciones);
        salida.flush();

        System.out.println("Enviando " + numeroCanciones + " canciones al cliente.");
        for (Cancion cancion : canciones) {
            salida.writeUTF(cancion.getNombre());
            System.out.println("Canción enviada: " + cancion.getNombre());
        }

        salida.writeUTF("fin_lista");
        salida.flush();
        System.out.println("Final de lista enviado al cliente.");
    }

    private void manejarDescargas() {
    try {
        while (true) {
            String mensaje = (String) entrada.readObject(); // Lee mensaje del cliente
            System.out.println("Mensaje recibido del cliente: " + mensaje);

            if ("logout".equals(mensaje)) {
                System.out.println("Cliente solicitó logout. Finalizando sesión.");
                finalizarSesion();
                break; // Salir del bucle y cerrar la conexión
            } else if ("actualizar".equals(mensaje)) {
                System.out.println("Cliente solicitó actualización de la lista de canciones.");
                enviarCancionesDisponibles();
            } else {
                // Procesar la solicitud de descarga
                try {
                    int opcionCancion = Integer.parseInt(mensaje);
                    List<Cancion> canciones = cancionDAO.obtenerCancionesDisponibles();

                    if (opcionCancion > 0 && opcionCancion <= canciones.size()) {
                        Cancion cancionSeleccionada = canciones.get(opcionCancion - 1);
                        String nombreCancion = cancionSeleccionada.getNombre();
                        String ubicacion = cancionSeleccionada.getUbicacion();

                        // Enviar el nombre de la canción al cliente
                        System.out.println("Enviando el nombre de la canción: " + nombreCancion);
                        salida.writeObject(nombreCancion);
                        salida.flush();

                        // Abrir el archivo de la canción
                        InputStream archivoCancion = getClass().getClassLoader().getResourceAsStream(ubicacion);

                        if (archivoCancion == null) {
                            salida.writeObject("error_archivo_no_encontrado");
                            salida.flush();
                            System.out.println("Error: El archivo no existe en el classpath en la ubicación: " + ubicacion);
                            continue;
                        }

                        // Enviar el archivo en bloques sin cerrar la conexión
                        try (BufferedInputStream bis = new BufferedInputStream(archivoCancion)) {
                            byte[] buffer = new byte[4096];
                            int bytesLeidos;
                            while ((bytesLeidos = bis.read(buffer)) != -1) {
                                salida.write(buffer, 0, bytesLeidos);
                            }
                            salida.flush(); // Asegurarse de que todo el archivo se envíe
                        }
                        System.out.println("Archivo enviado exitosamente: " + nombreCancion + " al usuario: " + clienteVO.getUsuario());
                        costoSesion += 15000;

                        // Enviar mensaje de finalización de descarga
                        salida.writeObject("descarga_completada");
                        salida.flush();
                    } else {
                        salida.writeObject("error_opcion_invalida");
                        salida.flush();
                        System.out.println("Error: Opción de canción inválida recibida del cliente.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Mensaje no reconocido: " + mensaje);
                    salida.writeObject("error_formato_mensaje");
                    salida.flush();
                }
            }
        }
    } catch (IOException | SQLException | ClassNotFoundException e) {
        System.out.println("Error en la transferencia: " + e.getMessage());
        e.printStackTrace();
    }
}

    public void finalizarSesion() throws IOException, SQLException {
    if (clienteVO != null) {
        // Calculamos la nueva deuda
        double nuevaDeuda = clienteVO.getDeuda() + costoSesion;
        clienteVO.setDeuda(nuevaDeuda);
        
        // Actualizamos en la base de datos
        clienteDAO.actualizarEstadoPago(clienteVO.getUsuario(), nuevaDeuda);
        
        // Enviamos el costo de la sesión al cliente
        salida.writeObject(String.format("Total a pagar: $%.2f", costoSesion));
        salida.writeObject("Sesión terminada");
        salida.flush();
        
        System.out.println("Sesión finalizada para el usuario: " + clienteVO.getUsuario());
        System.out.println("Costo de la sesión: $" + costoSesion);
        System.out.println("Nueva deuda total: $" + nuevaDeuda);
    } else {
        salida.writeObject("Error: No hay sesión activa");
        salida.writeObject("Sesión terminada");
        salida.flush();
    }
}

    private void cerrarConexion() {
        try {
            if (salida != null) {
                salida.close();
            }
            if (entrada != null) {
                entrada.close();
            }
            if (socket != null) {
                socket.close();
            }
            System.out.println("Conexión del cliente cerrada.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
