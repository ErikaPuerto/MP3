    package edu.avanzada.mp3.vista;
    import javax.swing.JOptionPane;

    public class ControladorVentana {

        // Método para mostrar un mensaje de información
        public void mostrarMensaje(String mensaje) {
            JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    public void mostrarMensajeSystem(String mensaje) {
            System.out.println(mensaje);
        }
        // Método para mostrar un mensaje de error
        public void mostrarError(String mensaje) {
            JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Método para mostrar un mensaje de advertencia
        public void mostrarAdvertencia(String mensaje) {
            JOptionPane.showMessageDialog(null, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

        // Método para mostrar opciones (Ej. Confirmar acción)
        public boolean mostrarConfirmacion(String mensaje) {
            int resultado = JOptionPane.showConfirmDialog(null, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);
            return resultado == JOptionPane.YES_OPTION;
        }
        // Método para mostrar un Input Dialog con mensaje y valor por defecto
        public String mostrarInputDialog(String mensaje, String valorPorDefecto) {
            return JOptionPane.showInputDialog(null, mensaje, valorPorDefecto);
        }
    }
