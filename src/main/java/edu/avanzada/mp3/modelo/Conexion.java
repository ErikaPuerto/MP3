package edu.avanzada.mp3.modelo;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase para gestionar la conexión a la base de datos utilizando JDBC.
 * La conexión es establecida y administrada de manera singleton.
 */
public class Conexion {
    private static Conexion instancia;
    private Connection conexion;
    private String url;
    private String usuario;
    private String contrasena;
    private final IMensajeria mensajeria;

    /**
     * Constructor privado para inicializar la conexión.
     * Carga los datos de conexión desde un archivo de propiedades y establece la conexión.
     * @param mensajeria Instancia que maneja la mensajería para mostrar mensajes del sistema.
     */
    private Conexion(IMensajeria mensajeria) {
        this.mensajeria = mensajeria;
        cargarDatosConexion();
        conectar();
    }

    /**
     * Inicializa la conexión de forma singleton.
     * Si ya existe una instancia, devuelve la existente.
     * 
     * @param mensajeria Instancia de la mensajería para mostrar mensajes del sistema.
     * @return La instancia única de la clase Conexion.
     */
    public static Conexion inicializar(IMensajeria mensajeria) {
        if (instancia == null) {
            instancia = new Conexion(mensajeria);
        }
        return instancia;
    }

    /**
     * Obtiene la instancia de la clase Conexion.
     * 
     * @return La instancia única de la clase Conexion.
     * @throws IllegalStateException Si la instancia no ha sido inicializada.
     */
    public static Conexion getInstancia() {
        if (instancia == null) {
            throw new IllegalStateException("Conexion no ha sido inicializada. Llame a inicializar() primero.");
        }
        return instancia;
    }
    
    /**
     * Carga los datos de conexión desde un archivo de propiedades.
     * Los valores de URL, usuario y contraseña se leen desde el archivo "conexion.properties".
     */
    private void cargarDatosConexion() {
        Properties propiedades = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("conexion.properties")) {
            if (input == null) {
                mensajeria.mostrarMensajeSystem("No se encontró el archivo conexion.properties.");
                return;
            }
            propiedades.load(input);
            this.url = propiedades.getProperty("db.url");
            this.usuario = propiedades.getProperty("db.user");
            this.contrasena = propiedades.getProperty("db.password");
        } catch (Exception e) {
            mensajeria.mostrarMensajeSystem("Error al cargar los datos de conexión: " + e.getMessage());
        }
    }

    /**
     * Establece la conexión con la base de datos utilizando los datos cargados.
     */
    public void conectar() {
        try {
            conexion = DriverManager.getConnection(url, usuario, contrasena);
            mensajeria.mostrarMensajeSystem("Conexión establecida correctamente.");
        } catch (SQLException e) {
            mensajeria.mostrarMensajeSystem("Error al establecer la conexión: " + e.getMessage());
        }
    }

    /**
     * Obtiene la conexión a la base de datos
     * @return La conexión a la base de datos.
     */
    public Connection getConexion() {
        return conexion;
    }

    /**
     * Cierra la conexión a la base de datos si está abierta.
     */
    public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                mensajeria.mostrarMensajeSystem("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                mensajeria.mostrarMensajeSystem("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}