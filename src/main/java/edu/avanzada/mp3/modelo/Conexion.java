package edu.avanzada.mp3.modelo;

import edu.avanzada.mp3.vista.ControladorVentana;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Conexion {
    private static Conexion instancia;
    private Connection conexion;
    private String url;
    private String usuario;
    private String contrasena;
    private ControladorVentana mensajes;

    // Constructor privado para garantizar que solo haya una instancia
    private Conexion() {
        this.mensajes = new ControladorVentana();
        cargarDatosConexion();
        conectar();
    }

    // Método para obtener la instancia única de la clase Conexion (Singleton)
    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    private void cargarDatosConexion() {
        Properties propiedades = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("conexion.properties")) {
            if (input == null) {
                mensajes.mostrarMensajeSystem("No se encontró el archivo conexion.properties.");
                return;
            }
            propiedades.load(input);
            this.url = propiedades.getProperty("db.url");
            this.usuario = propiedades.getProperty("db.user");
            this.contrasena = propiedades.getProperty("db.password");
        } catch (Exception e) {
            mensajes.mostrarMensajeSystem("Error al cargar los datos de conexión: " + e.getMessage());
        }
    }

    private void conectar() {
        try {
            conexion = DriverManager.getConnection(url, usuario, contrasena);
            mensajes.mostrarMensajeSystem("Conexión establecida correctamente.");
        } catch (SQLException e) {
            mensajes.mostrarMensajeSystem("Error al establecer la conexión: " + e.getMessage());
        }
    }

    public Connection getConexion() {
        return conexion;
    }

    public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                mensajes.mostrarMensajeSystem("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                mensajes.mostrarMensajeSystem("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
