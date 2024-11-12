package edu.avanzada.mp3.control;

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

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.iniciarConexion();
    }

    public Cliente() {this.controladorCliente = new ControladorCliente(socket);}

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
            System.out.println("Conexión establecida con el servidor en " + host + ":" + puerto);
            // Después de conectar, solicitar usuario
            autenticarUsuario();
        } catch (IOException e) {
            System.out.println("Error al conectar al servidor: " + e.getMessage());
            cerrarConexion(); // Llamada a cerrarConexion en caso de error
        }
    }

    // Método para autenticar al usuario (enviar usuario y contraseña)
    public void autenticarUsuario() {
    controladorCliente = new ControladorCliente(socket);
    try (Scanner scanner = new Scanner(System.in)) {
        System.out.println("Enviando usuario y contraseña al servidor...");
        // Solicitar usuario y contraseña
        System.out.print("Ingrese su usuario: ");
        String usuario = scanner.nextLine();
        this.nombreUsuario = usuario;
        System.out.print("Ingrese su contraseña: ");
        String contrasena = scanner.nextLine();
        System.out.println("Esperando respuesta del servidor...");

        // Enviar usuario y contraseña al servidor
        out.writeObject(usuario); // Enviar usuario
        out.writeObject(contrasena); // Enviar contraseña
        out.flush(); // Asegurarse de enviar todos los datos

        // Recibir respuesta del servidor
        String respuesta = (String) in.readObject(); // Usa readObject en lugar de readUTF

        System.out.println("Respuesta del servidor: " + respuesta);

        // Si el login fue exitoso, proceder a mostrar las canciones
        if ("Autenticación exitosa".equals(respuesta)) {
            mostrarCanciones();
        } else {
            System.out.println("Error de autenticación: " + respuesta);
            cerrarConexion();  // Cerrar la conexión si la autenticación falla
        }
    } catch (EOFException e) {
        System.out.println("El servidor cerró la conexión de manera inesperada.");
        cerrarConexion();
    } catch (IOException | ClassNotFoundException e) {
        System.out.println("Error al comunicarse con el servidor: " + e.getMessage());
        e.printStackTrace();
        cerrarConexion();
    }
}

    // Método para recibir y mostrar la lista de canciones disponibles
    public void mostrarCanciones() {
        try {
            int numeroCanciones = in.readInt(); // Leer el número de canciones
            System.out.println("Número de canciones recibidas: " + numeroCanciones);

            if (numeroCanciones == 0) {
                System.out.println("No hay canciones disponibles.");
            } else {
                for (int i = 0; i < numeroCanciones; i++) {
                    String cancion = in.readUTF(); // Recibe el nombre de cada canción
                    System.out.println("Canción " + (i + 1) + ": " + cancion);
                }
            }

            // Leer el mensaje final de "fin_lista"
            String finLista = in.readUTF();
            if ("fin_lista".equals(finLista)) {
                System.out.println("Lista de canciones recibida correctamente.");
            } else {
                System.out.println("Error: no se recibió el final esperado de la lista.");
            }

            // Mostrar el menú después de que la lista se haya recibido correctamente
            mostrarMenu(numeroCanciones); // Mostrar el menú unificado

        } catch (IOException e) {
            System.out.println("Error al comunicarse con el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarMenu(int numeroCanciones) {
        Scanner scanner = new Scanner(System.in);
        int opcion = -1;
        
        while (opcion != 3) { // Permitir que el usuario elija descargar, actualizar o salir
            System.out.println("Opciones:");
            System.out.println("1. Descargar una canción");
            System.out.println("2. Actualizar lista de canciones");
            System.out.println("3. Salir");

            System.out.print("Seleccione una opción: ");
            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                switch (opcion) {
                    case 1:
                        if (numeroCanciones > 0) {
                            seleccionarYDescargarCancion(numeroCanciones);
                        } else {
                            System.out.println("No hay canciones disponibles para descargar. Por favor, elija otra opción.");
                        }
                        break;
                        
                    case 2:
                        enviarSolicitudActualizar();
                        break;
                        
                    case 3:
                        System.out.println("Cerrando sesion...");
                        cerrarConexion();
                        break;
                        
                    default:
                        System.out.println("Opción inválida. Por favor, elija entre 1 y 3.");
                }
            } else {
                System.out.println("Por favor ingrese un número válido.");
                scanner.next(); // Limpiar el buffer si la entrada no es un número
            }
        }
    }

    // Método para seleccionar y descargar una canción
    private void seleccionarYDescargarCancion(int numeroCanciones) {
    Scanner scanner = new Scanner(System.in);
    int opcion = -1;
    while (opcion < 1 || opcion > numeroCanciones) {
        System.out.print("Ingrese el número de la canción que desea descargar (1-" + numeroCanciones + "): ");
        if (scanner.hasNextInt()) {
            opcion = scanner.nextInt();
            if (opcion < 1 || opcion > numeroCanciones) {
                System.out.println("Opción inválida, por favor elija un número entre 1 y " + numeroCanciones);
            }
        } else {
            System.out.println("Por favor ingrese un número válido.");
            scanner.next();
        }
    }

    try {
        // Enviar la opción al servidor
        out.writeObject(String.valueOf(opcion));

        // Recibir el nombre de la canción o mensaje de error
        String respuesta = (String) in.readObject();

        if (respuesta.startsWith("error_")) {
            System.out.println("Error: " + respuesta.substring(6).replace('_', ' '));
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

            System.out.println("Iniciando descarga de " + nombreCancion);
            System.out.println("Se guardará como: " + nombreArchivo);

            // Recibir el archivo sin conocer el tamaño previamente
            while ((bytesLeidos = in.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesLeidos);
            }

            System.out.println("\nDescarga completada: " + rutaDestino);
        }

        // Esperar el mensaje de confirmación de descarga completada
        String confirmacion = (String) in.readObject();
        if ("descarga_completada".equals(confirmacion)) {
            System.out.println("La descarga ha finalizado correctamente. Volviendo al menú...");
        } else {
            System.out.println("Error: No se recibió la confirmación esperada.");
        }

    } catch (EOFException e) {
        System.out.println("Error: Se alcanzó el final del flujo de datos inesperadamente. Verifica si el servidor cerró la conexión.");
    } catch (IOException | ClassNotFoundException e) {
        System.out.println("Error durante la descarga: " + e.getMessage());
        e.printStackTrace();
    }
}



    // Método para enviar una solicitud de actualización al servidor
    private void enviarSolicitudActualizar() {
        try {
            out.writeObject("actualizar"); // Enviar comando de actualización al servidor
            out.flush();
            System.out.println("Solicitando actualización de la lista de canciones...");

            mostrarCanciones(); // Recibir la lista actualizada de canciones

        } catch (IOException e) {
            System.out.println("Error al enviar solicitud de actualización: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para cerrar la conexión de red y liberar recursos
    public void cerrarConexion() {
    try {
        // Primero enviamos mensaje de logout al servidor
        out.writeObject("logout");
        out.flush();
        
        // Esperamos la respuesta del servidor con el costo de la sesión
        try {
            String costoTotal = (String) in.readObject();
            String mensajeFinal = (String) in.readObject();
            
            System.out.println(costoTotal);
            System.out.println(mensajeFinal);
        } catch (ClassNotFoundException e) {
            System.out.println("Error al recibir la respuesta final del servidor: " + e.getMessage());
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
        System.out.println("Conexión de cliente cerrada.");
        System.exit(0); // Cerramos la aplicación
    } catch (IOException e) {
        System.out.println("Error al cerrar la conexión del cliente: " + e.getMessage());
    }
}
}