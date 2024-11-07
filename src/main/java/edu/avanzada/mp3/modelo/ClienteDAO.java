package edu.avanzada.mp3.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDAO {

    /**
     * Método para autenticar un cliente en base al usuario y contraseña.
     * @param usuario El nombre de usuario del cliente.
     * @param contrasena La contraseña del cliente.
     * @return Un objeto ClienteVO si el cliente es encontrado, de lo contrario null.
     * @throws java.sql.SQLException
     */
    public ClienteVO autenticarCliente(String usuario, String contrasena) throws SQLException {
        // Obtenemos la conexión a la base de datos utilizando el Singleton de Conexion
        Connection conexion = Conexion.getInstancia().getConexion();
        
        // Consulta SQL para verificar la autenticación del cliente
        String sql = "SELECT * FROM Clientes WHERE usuario = ? AND contrasena = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, usuario);
        ps.setString(2, contrasena);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            // Si el cliente es encontrado, retornamos un objeto ClienteVO con los datos del cliente
            return new ClienteVO(rs.getInt("id_cliente"), rs.getString("usuario"), rs.getString("contrasena"), rs.getDouble("estado_pago"));
        }
        return null; // Si el cliente no es encontrado, retornamos null
    }

    /**
     * Método para actualizar el estado de pago de un cliente.
     * @param idCliente El id del cliente que realizará el pago.
     * @param nuevoPago El monto actualizado del estado de pago.
     * @throws SQLException Si ocurre un error al realizar la consulta.
     */
    public void actualizarEstadoPago(int idCliente, double nuevoPago) throws SQLException {
        // Obtenemos la conexión a la base de datos utilizando el Singleton de Conexion
        Connection conexion = Conexion.getInstancia().getConexion();
        
        // Consulta SQL para actualizar el estado de pago de un cliente
        String sql = "UPDATE Clientes SET estado_pago = ? WHERE id_cliente = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setDouble(1, nuevoPago);
        ps.setInt(2, idCliente);
        
        // Ejecutamos la actualización en la base de datos
        ps.executeUpdate();
    }
}
