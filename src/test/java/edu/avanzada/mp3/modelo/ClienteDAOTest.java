package edu.avanzada.mp3.modelo;

import edu.avanzada.mp3.vista.ControladorVentana;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteDAOTest {

    private ClienteDAO clienteDAO;
    private Connection mockConexion;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private ControladorVentana mockMensajes;

    @BeforeEach
    void setUp() throws SQLException {
        // Creamos los mocks
        mockConexion = Mockito.mock(Connection.class);
        mockPreparedStatement = Mockito.mock(PreparedStatement.class);
        mockResultSet = Mockito.mock(ResultSet.class);
        mockMensajes = Mockito.mock(ControladorVentana.class);

        // Asignamos el controlador de mensajes al DAO
        clienteDAO = new ClienteDAO();
        clienteDAO.setMensajes(mockMensajes);

        // Configuramos el PreparedStatement y ResultSet para las pruebas
        when(mockConexion.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void autenticarCliente_conUsuarioValido_devuelveClienteVO() throws SQLException {
        String usuario = "usuarioPrueba";
        String contrasena = "passwordPrueba";

        // Configuramos el resultado del mock para que devuelva los valores esperados
        when(mockResultSet.next()).thenReturn(true);  // Simula que se encuentra el usuario
        when(mockResultSet.getString("usuario")).thenReturn(usuario);
        when(mockResultSet.getString("contrasena")).thenReturn(contrasena);
        when(mockResultSet.getDouble("deuda")).thenReturn(100.0);

        ClienteVO cliente = clienteDAO.autenticarCliente(usuario, contrasena);

        // Verificaciones
        assertNotNull(cliente, "Se esperaba un ClienteVO no nulo, pero fue nulo.");
        assertEquals(usuario, cliente.getUsuario(), "El usuario no coincide.");
        assertEquals(contrasena, cliente.getContrasena(), "La contraseña no coincide.");
        assertEquals(100.0, cliente.getDeuda(), "La deuda no coincide.");
    }

    @Test
    void verificarDeuda_sinDeuda_devuelveTrue() throws SQLException {
        String usuario = "usuarioPrueba";

        // Configuramos el ResultSet para simular una deuda de 0
        when(mockResultSet.next()).thenReturn(true);  // Simula que se encuentra el usuario
        when(mockResultSet.getInt("deuda")).thenReturn(0);  // Deuda de 0

        int sinDeuda = clienteDAO.verificarDeuda(usuario);

        // Verificación
        assertTrue(sinDeuda, "Se esperaba true pero fue false.");
    }

    @Test
    void verificarDeuda_conDeuda_devuelveFalse() throws SQLException {
        String usuario = "usuarioPrueba";

        // Configuramos el ResultSet para simular una deuda de 100
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("deuda")).thenReturn(100);

        int sinDeuda = clienteDAO.verificarDeuda(usuario);

        assertFalse(sinDeuda);
    }

    @Test
    void autenticarCliente_conUsuarioInvalido_devuelveNull() throws SQLException {
        String usuario = "usuarioInexistente";
        String contrasena = "passwordPrueba";

        // Simula que el usuario no existe
        when(mockResultSet.next()).thenReturn(false);

        ClienteVO cliente = clienteDAO.autenticarCliente(usuario, contrasena);

        assertNull(cliente);
    }

    @Test
    void actualizarEstadoPago_actualizaCorrectamente() throws SQLException {
        String usuario = "usuarioPrueba";
        double nuevaDeuda = 200.0;

        doNothing().when(mockPreparedStatement).executeUpdate();

        clienteDAO.actualizarEstadoPago(usuario, (int) nuevaDeuda);

        verify(mockPreparedStatement).setDouble(1, nuevaDeuda);
        verify(mockPreparedStatement).setString(2, usuario);
        verify(mockPreparedStatement).executeUpdate();
    }

    @AfterEach
    void tearDown() {
        // Limpieza de recursos, si fuera necesario (en este caso no hay recursos reales que limpiar)
    }

    @AfterAll
    static void tearDownAll() {
        // Cierre de conexiones o limpieza global, si fuera necesario
    }

    private void assertTrue(int sinDeuda, String se_esperaba_true_pero_fue_false) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void assertFalse(int sinDeuda) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
