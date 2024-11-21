package edu.avanzada.mp3.modelo;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class ClienteDAOTest {

    private static Connection conexion;
    private ClienteDAO clienteDAO;

    private static final String CREATE_TABLE_CLIENTES = """
        CREATE TABLE clientes (
            usuario VARCHAR(50) PRIMARY KEY,
            contrasena VARCHAR(50) NOT NULL,
            deuda INT NOT NULL
        );
    """;

    @BeforeAll
    static void inicializarBD() throws SQLException {
        // Inicializar conexión a la base de datos en memoria
        Conexion.inicializar(new IMensajeria() {
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

        // Crear la base de datos en memoria
        conexion = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute(CREATE_TABLE_CLIENTES);
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Limpiar y poblar la base de datos antes de cada prueba
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute("DELETE FROM clientes;");
            stmt.execute("INSERT INTO clientes VALUES ('Juan', 'Sapo', 0);");
            stmt.execute("INSERT INTO clientes VALUES ('Julian', 'Julian', 0);");
            stmt.execute("INSERT INTO clientes VALUES ('Karina', 'Fachada', 0);");
            stmt.execute("INSERT INTO clientes VALUES ('Andres', '1234', 0);");
            stmt.execute("INSERT INTO clientes VALUES ('Sebastian', 'Mamma mia', 0);");
            stmt.execute("INSERT INTO clientes VALUES ('Mariana', 'Reptv', 0);");
            stmt.execute("INSERT INTO clientes VALUES ('Will', 'Tomi', 0);");
            stmt.execute("INSERT INTO clientes VALUES ('Donna', 'Sophie', 0);");
            stmt.execute("INSERT INTO clientes VALUES ('Isabel', '1322', 0);");
            stmt.execute("INSERT INTO clientes VALUES ('Jhon', 'Perros', 0);");
        }
        clienteDAO = new ClienteDAO(new IMensajeria() {
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
    void autenticarCliente_conCredencialesValidas_devuelveClienteVO() throws SQLException {
        ClienteVO cliente = clienteDAO.autenticarCliente("Juan", "Sapo");
        assertNotNull(cliente, "El cliente no debe ser nulo.");
        assertEquals("Juan", cliente.getUsuario());
        assertEquals("Sapo", cliente.getContrasena());
        assertEquals(0, cliente.getDeuda());
    }

    @Test
    void autenticarCliente_conCredencialesInvalidas() throws SQLException {
        ClienteVO cliente = clienteDAO.autenticarCliente("Juan", "Incorrecta");
        assertEquals("Juan", cliente.getUsuario());
        assertEquals("Sapo", cliente.getContrasena());
        assertEquals(0, cliente.getDeuda());
    }

    @Test
    void verificarDeuda_conDeudaExistente_devuelveCantidad() throws SQLException {
        int deuda = clienteDAO.verificarDeuda("Karina");
        assertEquals(0, deuda, "La deuda no coincide con la esperada.");
    }

    @Test
    void verificarDeuda_conUsuarioInexistente_devuelveCero() throws SQLException {
        int deuda = clienteDAO.verificarDeuda("Inexistente");
        assertEquals(0, deuda, "Se esperaba una deuda de 0 para un usuario inexistente.");
    }

    @Test
    void actualizarEstadoPago_actualizacionIncorrectamente() throws SQLException {
        clienteDAO.actualizarEstadoPago("Jhon", 0);
        int nuevaDeuda = clienteDAO.verificarDeuda("Jhon");
        assertEquals(70000, nuevaDeuda, "La deuda actualizada coincide.");
    }

    @AfterAll
    static void cerrarBD() throws SQLException {
        // Cerrar conexión después de todas las pruebas
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }
}