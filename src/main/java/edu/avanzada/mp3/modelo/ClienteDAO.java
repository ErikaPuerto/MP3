package edu.avanzada.mp3.modelo;

import edu.avanzada.mp3.vista.ControladorVentana;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDAO {

    private ControladorVentana mensajes;
     

    public ClienteDAO(){this.mensajes = new ControladorVentana();
    }
     public void setMensajes(ControladorVentana mensajes) {
        this.mensajes = mensajes;
    }
     

    /**
     * Método para autenticar un cliente en base al usuario y contraseña.
     *
     * @param usuario El nombre de usuario del cliente.
     * @param contrasena La contraseña del cliente.
     * @return Un objeto ClienteVO si el cliente es encontrado, de lo contrario null.
     * @throws java.sql.SQLException
     */
    public ClienteVO autenticarCliente(String usuario, String contrasena) throws SQLException {
        // Obtenemos la conexión a la base de datos utilizando el Singleton de Conexion
        Connection conexion = Conexion.getInstancia().getConexion();

        // Consulta SQL para verificar la autenticación del cliente
        String sql = "SELECT * FROM clientes WHERE usuario = ? AND contrasena = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, usuario);
        ps.setString(2, contrasena);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            // Si el cliente es encontrado, retornamos un objeto ClienteVO con los datos del cliente
            return new ClienteVO(rs.getString("usuario"), rs.getString("contrasena"), rs.getInt("deuda"));
        }
        return null; // Si el cliente no es encontrado, retornamos null
    }

    /**
     * Método para actualizar el estado de pago de un cliente en base al usuario.
     *
     * @param usuario El nombre de usuario del cliente cuyo estado de pago se actualizará.
     * @param nuevaDeuda El monto actualizado de la deuda del cliente.
     * @throws SQLException Si ocurre un error al realizar la consulta.
     */
    public void actualizarEstadoPago(String usuario, int nuevaDeuda) throws SQLException {
        Connection conexion = Conexion.getInstancia().getConexion();

        String sql = "UPDATE clientes SET deuda = ? WHERE usuario = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setInt(1, nuevaDeuda);
        ps.setString(2, usuario);
        // Ejecutamos la actualización en la base de datos
        ps.executeUpdate();
    }

    public int verificarDeuda(String usuario) throws SQLException {
        // Obtenemos la conexión a la base de datos utilizando el Singleton de Conexion
        Connection conexion = Conexion.getInstancia().getConexion();

        // Consulta SQL para verificar la autenticación del cliente
        String sql = "SELECT deuda FROM clientes WHERE usuario = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, usuario);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int deuda = rs.getInt("deuda");
            return deuda;
            
        } else {
            mensajes.mostrarMensajeSystem("Usuario no encontrado");
            return 0;
        }
    }
}
