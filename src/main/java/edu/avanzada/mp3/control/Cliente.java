package edu.avanzada.mp3.control;

import edu.avanzada.mp3.vista.ControladorVentana;

/**
 * Clase principal para el cliente, inicializa la conexión y los componentes de mensajería.
 */
public class Cliente {
    private ConexionCliente conexionCliente;
    private ControladorVentana mensajes;

    /**
     * Método principal que crea una instancia de {@code Cliente}.
     *
     * @param args argumentos de la línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.iniciarConexion();
    }

    /**
     * Constructor de {@code Cliente}, inicializa el controlador de ventana, el adaptador de mensajería
     * y la conexión con el cliente.
     */
    public Cliente() {
        this.mensajes = new ControladorVentana();
        this.conexionCliente = new ConexionCliente(mensajes);
    }
    
    /**
     * Metodo que llama a ConexionCliente para utilizar iniciarConexion localmente
     */
    public void iniciarConexion() {
        conexionCliente.iniciarConexion();
    }
}
