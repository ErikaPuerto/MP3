package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.Cancion;
import edu.avanzada.mp3.modelo.CancionDAO;
import edu.avanzada.mp3.modelo.ClienteDAO;
import edu.avanzada.mp3.modelo.ClienteVO;
import edu.avanzada.mp3.modelo.Conexion;
import edu.avanzada.mp3.modelo.IMensajeria;
import edu.avanzada.mp3.vista.ControladorVentana;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase {@code ControladorServidor} que gestiona la autenticación de clientes, la obtención de canciones disponibles,
 * y la actualización del estado de pago de los clientes.
 */
public class ControladorServidor {
    private ClienteDAO clienteDAO;
    private CancionDAO cancionDAO;
    private final IMensajeria mensajeria;

    /**
     * Constructor de {@code ControladorServidor}.
     * Inicializa el sistema de mensajería, la conexión y los DAOs necesarios.
     */
    public ControladorServidor() {
        // Inicializar el controlador de ventana y el adaptador de mensajería
        ControladorVentana controladorVentana = new ControladorVentana();
        this.mensajeria = new AdaptadorMensajeria(controladorVentana);

        // Inicializar la conexión con la mensajería
        Conexion.inicializar(mensajeria);

        // Inicializar los DAOs con la mensajería
        this.clienteDAO = new ClienteDAO(mensajeria);
        this.cancionDAO = new CancionDAO(mensajeria);
    }

    /**
     * Autentica a un cliente basado en el nombre de usuario y la contraseña proporcionados.
     *
     * @param usuario    el nombre de usuario del cliente
     * @param contrasena la contraseña del cliente
     * @return un objeto {@code ClienteVO} si la autenticación es exitosa; de lo contrario, {@code null}
     * @throws SQLException si ocurre un error de acceso a la base de datos
     */
    public ClienteVO autenticarCliente(String usuario, String contrasena) throws SQLException {
        return clienteDAO.autenticarCliente(usuario, contrasena);
    }

    /**
     * Obtiene la lista de canciones disponibles en la base de datos.
     *
     * @return una lista de objetos {@code Cancion} que representan las canciones disponibles
     * @throws SQLException si ocurre un error de acceso a la base de datos
     */
    public List<Cancion> obtenerCancionesDisponibles() throws SQLException {
        return cancionDAO.obtenerCancionesDisponibles();
    }

    /**
     * Actualiza el estado de pago del cliente con el monto proporcionado.
     *
     * @param usuario   el nombre de usuario del cliente
     * @param nuevoPago el nuevo monto de pago que se desea actualizar
     * @throws SQLException si ocurre un error de acceso a la base de datos
     */
    public void actualizarEstadoPago(String usuario, int nuevoPago) throws SQLException {
        clienteDAO.actualizarEstadoPago(usuario, nuevoPago);
    }
}
