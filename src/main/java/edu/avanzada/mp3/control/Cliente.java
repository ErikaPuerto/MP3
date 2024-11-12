package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.ClienteDAO;
import edu.avanzada.mp3.vista.ControladorVentana;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


public class Cliente {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String nombreUsuario;
    private ControladorCliente controladorCliente;
    private ClienteDAO clienteDAO;
    private ControladorVentana mensajes;

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.iniciarConexion();
    }

    public Cliente() {
        this.controladorCliente = new ControladorCliente(socket);
        this.clienteDAO = new ClienteDAO();
        this.mensajes = new ControladorVentana();
    }

    // Método para iniciar la conexión
    public void iniciarConexion() {
        String ipServidor = JOptionPane.showInputDialog("Introducir IP_SERVER:", "localhost");
        int puerto = 8080;
        conectarAlServidor(ipServidor, puerto);
    }

    // Método para conectar al servidor
    public void conectarAlServidor(String host, int puerto) {
        try {
            socket = new Socket(host, puerto);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            mensajes.mostrarMensajeSystem("Conexión establecida con el servidor en " + host + ":" + puerto);
            // Después de conectar, solicitar usuario
            autenticarUsuario();
        } catch (IOException e) {
            mensajes.mostrarError("Error al conectar al servidor: " + e.getMessage());
            cerrarConexion(); // Llamada a cerrarConexion en caso de error
        }
    }

    // Método para autenticar al usuario (enviar usuario y contraseña)
    public void autenticarUsuario() {
    controladorCliente = new ControladorCliente(socket);
    try {
        mensajes.mostrarMensajeSystem("Enviando usuario y contraseña al servidor...");
        // Solicitar usuario y contraseña
        String usuario = mensajes.mostrarInputDialog("Ingrese su usuario: ","usuario");
        //String usuario = scanner.nextLine();
        this.nombreUsuario = usuario;
        String contrasena = mensajes.mostrarInputDialog("Ingrese su contraseña: ", "contraseña");
        //String contrasena = scanner.nextLine();
        mensajes.mostrarMensajeSystem("Esperando respuesta del servidor...");

        // Enviar usuario y contraseña al servidor
        out.writeObject(usuario); // Enviar usuario
        out.writeObject(contrasena); // Enviar contraseña
        out.flush(); // Asegurarse de enviar todos los datos

        // Recibir respuesta del servidor
        String respuesta = (String) in.readObject(); // Usa readObject en lugar de readUTF

        mensajes.mostrarMensajeSystem("Respuesta del servidor: " + respuesta);

        // Si el login fue exitoso, proceder a mostrar las canciones
        if ("Autenticación exitosa".equals(respuesta)) {
            mostrarCanciones();
        } else {
            mensajes.mostrarError("Error de autenticación: " + respuesta);
            cerrarConexion();  // Cerrar la conexión si la autenticación falla
        }
    } catch (EOFException e) {
        mensajes.mostrarMensajeSystem("El servidor cerró la conexión de manera inesperada.");
        cerrarConexion();
    } catch (IOException | ClassNotFoundException e) {
        mensajes.mostrarError("Error al comunicarse con el servidor: " + e.getMessage());
        e.printStackTrace();
        cerrarConexion();
    }
}

    // Método para recibir y mostrar la lista de canciones disponibles
    public void mostrarCanciones() {
        try {
            int numeroCanciones = in.readInt(); // Leer el número de canciones
            mensajes.mostrarMensajeSystem("Número de canciones recibidas: " + numeroCanciones);

            if (numeroCanciones == 0) {
                mensajes.mostrarMensaje("No hay canciones disponibles.");
            } else {
                for (int i = 0; i < numeroCanciones; i++) {
                    String cancion = in.readUTF(); // Recibe el nombre de cada canción
                    mensajes.mostrarMensajeSystem("Canción " + (i + 1) + ": " + cancion);
                }
            }

            // Leer el mensajes final de "fin_lista"
            String finLista = in.readUTF();
            if ("fin_lista".equals(finLista)) {
                mensajes.mostrarMensajeSystem("Lista de canciones recibida correctamente.");
            } else {
                mensajes.mostrarError("Error: no se recibió el final esperado de la lista.");
            }

            // Mostrar el menú después de que la lista se haya recibido correctamente
            mostrarMenu(numeroCanciones); // Mostrar el menú unificado

        } catch (IOException e) {
            mensajes.mostrarError("Error al comunicarse con el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarMenu(int numeroCanciones) {
        Scanner scanner = new Scanner(System.in);
        int opcion = -1;
        
        while (opcion != 3) { // Permitir que el usuario elija descargar, actualizar o salir
            mensajes.mostrarMensajeSystem("Opciones:");
            mensajes.mostrarMensajeSystem("1. Descargar una canción");
            mensajes.mostrarMensajeSystem("2. Actualizar lista de canciones");
            mensajes.mostrarMensajeSystem("3. Salir");

            System.out.print("Seleccione una opción: ");
            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                switch (opcion) {
                    case 1:
                        if (numeroCanciones > 0){
                            try{
                                if (clienteDAO.verificarDeuda(nombreUsuario)){
                                    seleccionarYDescargarCancion(numeroCanciones);
                                }else{
                                    mensajes.mostrarMensaje("Tienes una deuda pendiente. No puedes descargar canciones");
                                    mensajes.mostrarMensaje("Plazo maximo de pago: 1 (una) semana");
                                    cerrarConexion();
                                }
                            }catch (SQLException e){
                                mensajes.mostrarMensaje("Error al verificar la deuda: " + e.getMessage());
                            }
                        }else {
                            mensajes.mostrarMensaje("No hay canciones disponibles para descargar. Por favor, elija otra opción.");
                        }
                        break;
                        
                    case 2:
                        enviarSolicitudActualizar();
                        break;
                        
                    case 3:
                        mensajes.mostrarMensaje("Cerrando sesion...");
                        cerrarConexion();
                        break;
                        
                    default:
                        mensajes.mostrarMensaje("Opción inválida. Por favor, elija entre 1 y 3.");
                }
            } else {
                mensajes.mostrarMensaje("Por favor ingrese un número válido.");
                scanner.next(); // Limpiar el buffer si la entrada no es un número
            }
        }
    }

    // Método para seleccionar y descargar una canción
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

        // Esperar el mensajes de confirmación de descarga completada
        String confirmacion = (String) in.readObject();
        if ("descarga_completada".equals(confirmacion)) {
            mensajes.mostrarMensajeSystem("La descarga ha finalizado correctamente. Volviendo al menú...");
        } else {
            mensajes.mostrarMensajeSystem("Error: No se recibió la confirmación esperada.");
        }

    } catch (EOFException e) {
        System.out.println("Error: Se alcanzó el final del flujo de datos inesperadamente. Verifica si el servidor cerró la conexión.");
    } catch (IOException | ClassNotFoundException e) {
        mensajes.mostrarMensajeSystem("Error durante la descarga: " + e.getMessage());
        e.printStackTrace();
    }
}



    // Método para enviar una solicitud de actualización al servidor
    private void enviarSolicitudActualizar() {
        try {
            out.writeObject("actualizar"); // Enviar comando de actualización al servidor
            out.flush();
            mensajes.mostrarMensajeSystem("Solicitando actualización de la lista de canciones...");

            mostrarCanciones(); // Recibir la lista actualizada de canciones

        } catch (IOException e) {
            mensajes.mostrarError("Error al enviar solicitud de actualización: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para cerrar la conexión de red y liberar recursos
    public void cerrarConexion() {
    try {
        // Primero enviamos mensajes de logout al servidor
        out.writeObject("logout");
        out.flush();
        
        // Esperamos la respuesta del servidor con el costo de la sesión
        try {
            String costoTotal = (String) in.readObject();
            String mensajeFinal = (String) in.readObject();
            
            mensajes.mostrarMensajeSystem(costoTotal);
            mensajes.mostrarMensajeSystem(mensajeFinal);
        } catch (ClassNotFoundException e) {
            mensajes.mostrarMensajeSystem("Error al recibir la respuesta final del servidor: " + e.getMessage());
        }

        // Cerramos los recursos
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
        System.exit(0); // Cerramos la aplicación
    } catch (IOException e) {
        mensajes.mostrarMensajeSystem("Error al cerrar la conexión del cliente: " + e.getMessage());
    }
}
}