package edu.avanzada.mp3.control;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public static void main(String[] args) {
        Cliente cliente = new Cliente("localhost", 8080);
        cliente.conectarAlServidor();
    }

    public Cliente(String host, int puerto) {
        try {
            socket = new Socket(host, puerto);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Conexión establecida con el servidor en " + host + ":" + puerto);
        } catch (IOException e) {
            System.out.println("Error al conectar al servidor: " + e.getMessage());
            cerrarConexion(); // Llamada a cerrarConexion en caso de error
        }
    }

    // Método para conectar al servidor
    public void conectarAlServidor() {
        try {
            // Conectar al servidor (dirección IP del servidor y puerto)
            socket = new Socket("localhost", 8080);  // Asegúrate de que el servidor está en el puerto 8080
            System.out.println("Conectado al servidor.");

            // Crear flujos de entrada y salida
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            // Iniciar la autenticación
            autenticarUsuario();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para autenticar al usuario (enviar usuario y contraseña)
    public void autenticarUsuario() {
        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("Enviando usuario y contraseña al servidor...");
            // Solicitar usuario y contraseña
            System.out.print("Ingrese su usuario: ");
            String usuario = scanner.nextLine();
            System.out.print("Ingrese su contraseña: ");
            String contrasena = scanner.nextLine();
            System.out.println("Esperando respuesta del servidor...");

            // Enviar usuario y contraseña al servidor usando writeObject
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
            }

        } catch (EOFException e) {
            System.out.println("El servidor cerró la conexión de manera inesperada.");
            cerrarConexion();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al comunicarse con el servidor: " + e.getMessage());
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
                        cerrarConexion();
                        System.out.println("Saliendo del programa.");
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

// Método para solicitar una canción al servidor
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
                scanner.next(); // Limpiar el buffer si la entrada no es un número
            }
        }

        try {
            out.writeInt(opcion); // Enviar la elección al servidor
            String mensaje = in.readUTF(); // Recibir el estado de la descarga
            System.out.println(mensaje);
        } catch (IOException e) {
            System.out.println("Error al comunicarse con el servidor: " + e.getMessage());
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
        } catch (IOException e) {
            System.out.println("Error al cerrar la conexión del cliente: " + e.getMessage());
        }
    }
}
