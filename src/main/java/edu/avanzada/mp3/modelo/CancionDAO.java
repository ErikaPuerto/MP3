package edu.avanzada.mp3.modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que maneja el acceso a datos de las canciones en la base de datos.
 * Proporciona métodos para obtener listas de canciones y buscar canciones individuales.
 */
public class CancionDAO {
    private final IMensajeria mensajeria;

    /**
     * Constructor de la clase CancionDAO
     * @param mensajeria Objeto para manejar mensajes y errores.
     */
    public CancionDAO(IMensajeria mensajeria) {
        this.mensajeria = mensajeria;
    }

    /**
     * Obtiene una lista de todas las canciones disponibles en la base de datos.
     * @return Lista de canciones disponibles.
     * @throws SQLException Si ocurre un error en la conexión o consulta a la base de datos.
     */
    public List<Cancion> obtenerCancionesDisponibles() throws SQLException {
        Connection conexion = Conexion.getInstancia().getConexion();
        String sql = "SELECT * FROM canciones";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Cancion> canciones = new ArrayList<>();
        
        while (rs.next()) {
            Cancion cancion = new Cancion(
                rs.getString("nombre"),
                rs.getString("artista"),
                rs.getString("ubicacion")
            );
            canciones.add(cancion);
        }
        return canciones;
    }
    
    /**
     * Obtiene una canción de la lista según la posición especificada.
     * 
     * @param posicion Posición de la canción en la lista.
     * @return Cancion en la posición indicada.
     * @throws SQLException Si ocurre un error al obtener la lista de canciones.
     * @throws IndexOutOfBoundsException Si la posición es inválida.
     */
    public Cancion obtenerCancionPorPosicion(int posicion) throws SQLException {
        List<Cancion> canciones = obtenerCancionesDisponibles();
        if (posicion >= 0 && posicion < canciones.size()) {
            return canciones.get(posicion);
        } else {
            throw new IndexOutOfBoundsException("La posición especificada no es válida.");
        }
    }

    /**
     * Encuentra y devuelve el nombre del artista de una canción en una posición específica.
     * 
     * @param o Índice de la canción.
     * @return Nombre del artista de la canción o una cadena vacía si no se encuentra.
     */
    public String encontrarartistaCancion(int o) {
        Cancion cancion = null;
        try {
            cancion = obtenerCancionPorPosicion(o);
        } catch (SQLException e) {
            mensajeria.mostrarError("Error al obtener la canción: " + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            mensajeria.mostrarError(e.getMessage());
        }
        return cancion != null ? cancion.getArtista() : "";
    }
}