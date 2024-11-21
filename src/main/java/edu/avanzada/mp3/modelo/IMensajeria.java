package edu.avanzada.mp3.modelo;

/**
 * Interfaz que define los métodos para la mensajería dentro del sistema.
 * Los métodos proporcionan formas de mostrar mensajes, advertencias, errores,
 * confirmaciones y cuadros de entrada en la interfaz de usuario.
 */
public interface IMensajeria {
    void mostrarMensaje(String mensaje);
    void mostrarError(String mensaje);
    void mostrarAdvertencia(String mensaje);
    void mostrarMensajeSystem(String mensaje);
    boolean mostrarConfirmacion(String mensaje);
    String mostrarInputDialog(String mensaje, String valorPorDefecto);
}