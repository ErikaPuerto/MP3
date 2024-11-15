package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.Cancion;
import edu.avanzada.mp3.modelo.CancionDAO;
import edu.avanzada.mp3.modelo.ClienteDAO;
import edu.avanzada.mp3.modelo.ClienteVO;
import java.sql.SQLException;
import java.util.List;

public class ControladorServidor {
    private ClienteDAO clienteDAO;
    private CancionDAO cancionDAO;

    public ControladorServidor() {
        clienteDAO = new ClienteDAO();
        cancionDAO = new CancionDAO();
    }

    public ClienteVO autenticarCliente(String usuario, String contrasena) throws SQLException {
        return clienteDAO.autenticarCliente(usuario, contrasena);
    }

    public List<Cancion> obtenerCancionesDisponibles() throws SQLException {
        return cancionDAO.obtenerCancionesDisponibles();
    }

    public void actualizarEstadoPago(String usuario, int nuevoPago) throws SQLException {
        clienteDAO.actualizarEstadoPago(usuario, nuevoPago);
    }
}
