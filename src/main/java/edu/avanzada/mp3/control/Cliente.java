package edu.avanzada.mp3.control;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.conectarAlServidor();
    }

    // Método para conectar al servidor
    public void conectarAlServidor() {
        try {
            // Conectar al servidor (dirección IP del servidor y puerto)
            socket = new Socket("localhost", 8080);  // Asegúrate de que el servidor está en el puerto 8080
            System.out.println("Conectado al servidor.");

            // Crear flujos de entrada y salida
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // Iniciar la autenticación
            autenticarUsuario();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para autenticar al usuario (enviar usuario y contraseña)
    public void autenticarUsuario() {
        try {
            Scanner scanner = new Scanner(System.in);

            // Solicitar usuario y contraseña
            System.out.print("Ingrese su usuario: ");
            String usuario = scanner.nextLine();
            System.out.print("Ingrese su contraseña: ");
            String contrasena = scanner.nextLine();

            // Enviar usuario y contraseña al servidor
            out.writeUTF(usuario);  // Enviar usuario
            out.writeUTF(contrasena); // Enviar contraseña

            // Recibir respuesta del servidor
            String respuesta = in.readUTF();
            System.out.println("Respuesta del servidor: " + respuesta);

            // Si el login fue exitoso, proceder a mostrar las canciones
            if (respuesta.equals("Autenticación exitosa")) {
                mostrarCanciones();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para recibir y mostrar la lista de canciones disponibles
    public void mostrarCanciones() {
        try {
            // El servidor enviará la lista de canciones
            int numeroCanciones = in.readInt();  // Asumimos que el servidor envía el número de canciones

            System.out.println("Canciones disponibles:");
            for (int i = 0; i < numeroCanciones; i++) {
                String cancion = in.readUTF();
                System.out.println((i + 1) + ". " + cancion);
            }

            // Permitir al usuario elegir una canción
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese el número de la canción que desea descargar: ");
            int opcion = scanner.nextInt();

            // Enviar la elección al servidor
            out.writeInt(opcion);

            // Recibir el estado de la descarga
            String mensaje = in.readUTF();
            System.out.println(mensaje);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
