package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.IMensajeria;
import edu.avanzada.mp3.vista.ControladorVentana;

/**
 * Adaptador para manejar la mensajería utilizando {@link ControladorVentana}.
 */
public class AdaptadorMensajeria implements IMensajeria {
    private final ControladorVentana controladorVentana;

    /**
     * Crea un adaptador de mensajería con el controlador de ventana especificado.
     *
     * @param controladorVentana el controlador de ventana a utilizar
     */
    public AdaptadorMensajeria(ControladorVentana controladorVentana) {
        this.controladorVentana = controladorVentana;
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        controladorVentana.mostrarMensaje(mensaje);
    }

    @Override
    public void mostrarError(String mensaje) {
        controladorVentana.mostrarError(mensaje);
    }

    @Override
    public void mostrarAdvertencia(String mensaje) {
        controladorVentana.mostrarAdvertencia(mensaje);
    }

    @Override
    public void mostrarMensajeSystem(String mensaje) {
        controladorVentana.mostrarMensajeSystem(mensaje);
    }

    @Override
    public boolean mostrarConfirmacion(String mensaje) {
        return controladorVentana.mostrarConfirmacion(mensaje);
    }

    @Override
    public String mostrarInputDialog(String mensaje, String valorPorDefecto) {
        return controladorVentana.mostrarInputDialog(mensaje, valorPorDefecto);
    }
}
