package edu.avanzada.mp3.control;

import edu.avanzada.mp3.vista.ControladorVentana;

public class Cliente {
    private ConexionCliente conexionCliente;
    private ControladorVentana mensajes;

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
    }

    public Cliente() {
        this.mensajes = new ControladorVentana();
        this.conexionCliente = new ConexionCliente(mensajes);
    }
}
