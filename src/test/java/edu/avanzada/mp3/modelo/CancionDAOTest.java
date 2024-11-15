package edu.avanzada.mp3.modelo;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CancionDAOTest {

    private static Connection conexion;
    private CancionDAO cancionDAO;

    @BeforeAll
    static void inicializarBaseDeDatos() throws SQLException {
        // Crear conexión a base de datos H2 en memoria
        conexion = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Conexion.getInstancia().setConexion(conexion);

        // Crear tabla de canciones
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute("CREATE TABLE canciones ("
                    + "nombre VARCHAR(100), "
                    + "artista VARCHAR(100), "
                    + "ubicacion VARCHAR(200))");
        }
    }

    @BeforeEach
    void configurar() throws SQLException {
        // Asegurarse de que tenemos una conexión válida
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
            Conexion.getInstancia().setConexion(conexion);
        }

        // Limpiar datos existentes
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute("DELETE FROM canciones");

            // Insertar datos de prueba
            stmt.execute("INSERT INTO canciones (nombre, artista, ubicacion) VALUES "
                    + "('Canción 1', 'Artista 1', '/musica/cancion1.mp3'),"
                    + "('Canción 2', 'Artista 2', '/musica/cancion2.mp3'),"
                    + "('Canción 3', 'Artista 3', '/musica/cancion3.mp3')");
        }

        cancionDAO = new CancionDAO();
    }

    @Test
    @DisplayName("Prueba exitosa: Obtener todas las canciones disponibles")
    void testObtenerCancionesDisponiblesExitoso() throws SQLException {
        // Ejecutar
        List<Cancion> canciones = cancionDAO.obtenerCancionesDisponibles();

        // Verificar
        assertNotNull(canciones, "La lista de canciones no debería ser null");
        assertEquals(3, canciones.size(), "Deberían haber 3 canciones");

        // Verificar la primera canción
        Cancion primerCancion = canciones.get(0);
        assertEquals("Canción 1", primerCancion.getNombre());
        assertEquals("Artista 1", primerCancion.getArtista());
        assertEquals("/musica/cancion1.mp3", primerCancion.getUbicacion());
    }

    @Test
    @DisplayName("Prueba exitosa: Lista vacía cuando no hay canciones")
    void testObtenerCancionesDisponiblesVacio() throws SQLException {
        // Preparar: Eliminar todas las canciones
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute("DELETE FROM canciones");
        }

        // Ejecutar
        List<Cancion> canciones = cancionDAO.obtenerCancionesDisponibles();

        // Verificar
        assertNotNull(canciones, "La lista no debería ser null aunque esté vacía");
        assertTrue(canciones.isEmpty(), "La lista debería estar vacía");
    }

    @Test
    @DisplayName("Prueba exitosa: Insertar y obtener canción")
    void testInsertarYObtenerCancion() throws SQLException {
        // Preparar: Insertar una nueva canción directamente
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute("INSERT INTO canciones (nombre, artista, ubicacion) VALUES "
                    + "('Canción 4', 'Artista 4', '/musica/cancion4.mp3')");
        }

        // Ejecutar: Obtener todas las canciones
        List<Cancion> canciones = cancionDAO.obtenerCancionesDisponibles();

        // Verificar: Comprobar que la canción 'Canción 4' esté en la lista
        assertNotNull(canciones, "La lista de canciones no debería ser null");
        assertTrue(canciones.stream().anyMatch(c -> c.getNombre().equals("Canción 4")), "La canción 'Canción 4' debería estar en la lista");
    }

    @Test
    @DisplayName("Prueba fallida: Consulta malformada")
    void testObtenerCancionesDisponiblesConsultaMalformada() throws SQLException {
        // Preparar: Ejecutar una consulta malformada (sin cláusula SELECT)
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute("SELECT * FORM canciones");  // "FORM" es incorrecto, debería ser "FROM"
        }

        // Ejecutar y verificar que lance excepción
        assertThrows(SQLException.class, () -> {
            cancionDAO.obtenerCancionesDisponibles();
        }, "Debería lanzar SQLException cuando la consulta SQL está malformada");
    }

    @Test
    @DisplayName("Prueba fallida: No hay conexión a la base de datos")
    void testObtenerCancionesSinConexion() throws SQLException {
        // Preparar: Simulamos que la conexión a la base de datos no está disponible
        Conexion.getInstancia().setConexion(null);

        // Ejecutar y verificar que lance excepción
        assertThrows(SQLException.class, () -> {
            cancionDAO.obtenerCancionesDisponibles();
        }, "Debería lanzar SQLException cuando no hay conexión a la base de datos");
    }

    @Test
    @DisplayName("Prueba fallida: Insertar canción con datos nulos")
    void testInsertarCancionConDatosNulos() throws SQLException {
        // Preparar: Intentar insertar una canción con un valor null en un campo obligatorio
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute("INSERT INTO canciones (nombre, artista, ubicacion) VALUES (null, 'Artista 5', '/musica/cancion5.mp3')");
        }

        // Ejecutar
        List<Cancion> canciones = cancionDAO.obtenerCancionesDisponibles();

        // Verificar: No debería haber canciones con nombre null
        assertFalse(canciones.stream().anyMatch(c -> c.getNombre() == null), "No se debería permitir insertar canciones con nombre null");
    }

    @AfterEach
    void limpiar() throws SQLException {
        if (!conexion.isClosed()) {
            try (Statement stmt = conexion.createStatement()) {
                stmt.execute("DELETE FROM canciones");
            }
        }
    }

    @AfterAll
    static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.reiniciarInstancia();
        }
    }
}
