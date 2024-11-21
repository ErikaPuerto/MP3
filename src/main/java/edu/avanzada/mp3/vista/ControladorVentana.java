    package edu.avanzada.mp3.vista;
    import javax.swing.JOptionPane;

    /**
 * Clase que proporciona métodos para mostrar mensajes de información, error, advertencia, 
 * confirmación y cuadros de entrada al usuario utilizando {@link JOptionPane}.
 */
    public class ControladorVentana {

    /**
     * Muestra un mensaje de información al usuario en una ventana emergente.
     * @param mensaje El mensaje que se desea mostrar al usuario.
     */
        public void mostrarMensaje(String mensaje) {
            JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
        }
        
    /**
     * Muestra un mensaje del sistema en la consola.
     * @param mensaje El mensaje que se desea mostrar en la consola.
     */
    public void mostrarMensajeSystem(String mensaje) {
            System.out.println(mensaje);
        }
    /**
     * Muestra un mensaje de error al usuario en una ventana emergente.
     * @param mensaje El mensaje de error que se desea mostrar al usuario.
     */
        public void mostrarError(String mensaje) {
            JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }

    /**
     * Muestra un mensaje de advertencia al usuario en una ventana emergente.
     * @param mensaje El mensaje de advertencia que se desea mostrar al usuario.
     */
        public void mostrarAdvertencia(String mensaje) {
            JOptionPane.showMessageDialog(null, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

    /**
     * Muestra un cuadro de confirmación con opciones de "Sí" o "No".
     * @param mensaje El mensaje que se desea mostrar en el cuadro de confirmación.
     * @return `true` si el usuario selecciona "Sí", `false` si selecciona "No".
     */
        public boolean mostrarConfirmacion(String mensaje) {
            int resultado = JOptionPane.showConfirmDialog(null, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);
            return resultado == JOptionPane.YES_OPTION;
        }

    /**
     * Muestra un cuadro de entrada de texto al usuario con un mensaje y un valor por defecto.
     * 
     * @param mensaje El mensaje que se desea mostrar en el cuadro de entrada.
     * @param valorPorDefecto El valor por defecto que se debe mostrar en el cuadro de entrada.
     * @return El valor introducido por el usuario, o el valor por defecto si el usuario no modifica el campo.
     */
        public String mostrarInputDialog(String mensaje, String valorPorDefecto) {
            return JOptionPane.showInputDialog(null, mensaje, valorPorDefecto);
        }
    }
