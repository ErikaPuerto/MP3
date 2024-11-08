package edu.avanzada.mp3.modelo;

import edu.avanzada.mp3.modelo.Cancion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CancionDAO {

    /**
     * Método para obtener todas las canciones disponibles en la base de datos.
     * @return Una lista de objetos Cancion.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
    public List<Cancion> obtenerCancionesDisponibles() throws SQLException {
        // Obtenemos la conexión a la base de datos utilizando el Singleton de Conexion
        Connection conexion = Conexion.getInstancia().getConexion();
        
        // Consulta SQL para obtener todas las canciones disponibles
        String sql = "SELECT * FROM canciones";
        PreparedStatement ps = conexion.prepareStatement(sql);
        
        // Ejecutamos la consulta y obtenemos los resultados
        ResultSet rs = ps.executeQuery();
        List<Cancion> canciones = new ArrayList<>();
        
        while (rs.next()) {
            // Por cada resultado, creamos un objeto Cancion y lo agregamos a la lista
            Cancion cancion = new Cancion(rs.getString("nombre"), rs.getString("artista"), rs.getString("ubicacion"));
            canciones.add(cancion);
        }
        return canciones; // Retornamos la lista de canciones
    }
}
