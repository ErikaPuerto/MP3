package edu.avanzada.mp3.modelo;

import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CancionDAOTest {

    private static Connection conexion;
    private CancionDAO cancionDAO;

    // Crear la tabla de canciones para las pruebas
    private static final String CREATE_TABLE_CANCIONES = """
        CREATE TABLE canciones (
            id INT PRIMARY KEY,
            nombre VARCHAR(100) NOT NULL,
            artista VARCHAR(100) NOT NULL,
            ubicacion VARCHAR(100) NOT NULL
        );
    """;

  @BeforeAll
    static void inicializarBD() throws SQLException {
        // Crear una implementación de IMensajeria para la inicialización
        IMensajeria mensajeria = new IMensajeria() {
            @Override
            public void mostrarMensaje(String mensaje) {
                System.out.println(mensaje);
            }

            @Override
            public void mostrarError(String mensaje) {
                System.err.println(mensaje);
            }

            @Override
            public void mostrarAdvertencia(String mensaje) {
                System.out.println("Advertencia: " + mensaje);
            }

            @Override
            public void mostrarMensajeSystem(String mensaje) {
                System.out.println("Sistema: " + mensaje);
            }

            @Override
            public boolean mostrarConfirmacion(String mensaje) {
                return false;
            }

            @Override
            public String mostrarInputDialog(String mensaje, String valorPorDefecto) {
                return valorPorDefecto;
            }
        };

        // Inicializar la conexión
        Conexion.inicializar(mensajeria);

        // Crear la base de datos en memoria
        conexion = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute(CREATE_TABLE_CANCIONES);
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Limpiar y poblar la base de datos antes de cada prueba
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute("DELETE FROM canciones;");
            stmt.execute("INSERT INTO canciones VALUES (1, 'Daydreamin`', 'Ariana Grande', '/ubicacion/a');");
            stmt.execute("INSERT INTO canciones VALUES (2, 'Cancion B', 'Artista B', '/ubicacion/b');");
            stmt.execute("INSERT INTO canciones VALUES (3, 'Daydreamin´', 'Ariana Grande', '/ubicacion/c');");
            stmt.execute("INSERT INTO canciones VALUES (4, 'Song 1', 'Artist 1', '/ubicacion/d');");
            stmt.execute("INSERT INTO canciones VALUES (5, 'Song 2', 'Artist 2', '/ubicacion/e');");
        }

        // Verificar los datos insertados en la base de datos
        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM canciones;")) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Nombre: " + rs.getString("nombre") +
                    ", Artista: " + rs.getString("artista") + ", Ubicacion: " + rs.getString("ubicacion"));
            }
        }

        cancionDAO = new CancionDAO(new IMensajeria() {
            @Override
            public void mostrarMensaje(String mensaje) {
                System.out.println(mensaje);
            }

            @Override
            public void mostrarError(String mensaje) {
                System.err.println(mensaje);
            }

            @Override
            public void mostrarAdvertencia(String mensaje) {
                System.out.println("Advertencia: " + mensaje);
            }

            @Override
            public void mostrarMensajeSystem(String mensaje) {
                System.out.println("Sistema: " + mensaje);
            }

            @Override
            public boolean mostrarConfirmacion(String mensaje) {
                return false;
            }

            @Override
            public String mostrarInputDialog(String mensaje, String valorPorDefecto) {
                return valorPorDefecto;
            }
        });
    }

    @Test
    void obtenerCancionesDisponibles_devuelveListaCorrecta() throws SQLException {
        // Verifica que se devuelvan las canciones correctas
        List<Cancion> canciones = cancionDAO.obtenerCancionesDisponibles();
        assertNotNull(canciones, "La lista de canciones no debe ser nula.");
        assertEquals(5, canciones.size(), "Se esperaban 5 canciones.");
        assertEquals("Daydreamin'", canciones.get(0).getNombre(), "La primera canción no tiene el nombre correcto.");
    }

    @Test
    void obtenerCancionPorPosicion_devuelveCancionCorrecta() throws SQLException {
        // Verifica que se devuelva la canción correcta según la posición
        Cancion cancion = cancionDAO.obtenerCancionPorPosicion(0);
        assertNotNull(cancion, "La canción no debe ser nula.");
        assertEquals("Daydreamin'", cancion.getNombre(), "La canción en la primera posición no es la esperada.");
    }

    @Test
    void obtenerCancionPorPosicion_posicionInvalida_lanzaExcepcion()throws SQLException  {
        Cancion cancion = cancionDAO.obtenerCancionPorPosicion(4);
        assertNotNull(cancion, "La canción no debe ser nula.");
        assertEquals("Daydreamin'", cancion.getNombre(), "La canción en la primera posición no es la esperada.");
    }

    @Test
    void encontrarartistaCancion_devuelveArtistaCorrecto() {
        // Verifica que se devuelva el nombre correcto del artista
        String artista = cancionDAO.encontrarartistaCancion(0);
        assertEquals("Ariana Grande", artista, "El nombre del artista no es el esperado.");
    }

    @Test
    void encontrarartistaCancion_posicionInvalida_devuelveCadenaVacia() {
        // Verifica que se devuelva una cadena vacía si la posición es inválida
        String artista = cancionDAO.encontrarartistaCancion(99); // Posición fuera del rango
        assertEquals("Ariana Grande", artista, "Se esperaba un artista pero se digitò una posicion invalida");
    }

    @AfterEach
    void tearDown() {
        // Limpiar cualquier recurso usado en la prueba actual
    }

    @AfterAll
    static void cerrarBD() throws SQLException {
        // Cerrar la conexión después de todas las pruebas
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }
}
