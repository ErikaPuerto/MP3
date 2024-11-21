package edu.avanzada.mp3.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que maneja el acceso a datos de los clientes en la base de datos.
 * Proporciona métodos para autenticar clientes, actualizar deudas y verificar el estado de pago.
 */
public class ClienteDAO {
    private final IMensajeria mensajeria;

    /**
     * Constructor de la clase ClienteDAO.
     * @param mensajeria Objeto que maneja los mensajes de salida y errores.
     */
    public ClienteDAO(IMensajeria mensajeria) {
        this.mensajeria = mensajeria;
    }

    /**
     * Autentica a un cliente mediante el nombre de usuario y la contraseña.
     * 
     * @param usuario Nombre de usuario del cliente.
     * @param contrasena Contraseña del cliente.
     * @return Un objeto ClienteVO con los datos del cliente si las credenciales son válidas, 
     *         o null si no se encuentra.
     * @throws SQLException Si ocurre un error en la consulta a la base de datos.
     */
    public ClienteVO autenticarCliente(String usuario, String contrasena) throws SQLException {
        Connection conexion = Conexion.getInstancia().getConexion();
        String sql = "SELECT * FROM clientes WHERE usuario = ? AND contrasena = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, usuario);
        ps.setString(2, contrasena);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return new ClienteVO(
                rs.getString("usuario"),
                rs.getString("contrasena"),
                rs.getInt("deuda")
            );
        }
        return null;
    }

    /**
     * Actualiza el estado de pago de un cliente, estableciendo una nueva deuda.
     * 
     * @param usuario Nombre de usuario del cliente.
     * @param nuevaDeuda Nueva cantidad de deuda del cliente.
     * @throws SQLException Si ocurre un error en la actualización de la base de datos.
     */
    public void actualizarEstadoPago(String usuario, int nuevaDeuda) throws SQLException {
        Connection conexion = Conexion.getInstancia().getConexion();
        String sql = "UPDATE clientes SET deuda = ? WHERE usuario = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setInt(1, nuevaDeuda);
        ps.setString(2, usuario);
        ps.executeUpdate();
    }

    /**
     * Verifica la deuda actual de un cliente.
     * 
     * @param usuario Nombre de usuario del cliente.
     * @return Cantidad de deuda del cliente o 0 si no se encuentra el usuario.
     * @throws SQLException Si ocurre un error en la consulta a la base de datos.
     */
    public int verificarDeuda(String usuario) throws SQLException {
        Connection conexion = Conexion.getInstancia().getConexion();
        String sql = "SELECT deuda FROM clientes WHERE usuario = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, usuario);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return rs.getInt("deuda");
        } else {
            mensajeria.mostrarMensajeSystem("Usuario no encontrado");
            return 0;
        }
    }
}