package edu.avanzada.mp3.modelo;

/**
 * Clase de valor que representa un cliente en el sistema.
 * Contiene información sobre el usuario, su contraseña y su deuda.
 */
public class ClienteVO {

    private String usuario;
    private String contrasena;
    private int deuda;

    /**
     * Constructor de la clase ClienteVO.
     * 
     * @param usuario Nombre de usuario del cliente.
     * @param contrasena Contraseña del cliente.
     * @param deuda Cantidad de deuda del cliente.
     */
    public ClienteVO(String usuario, String contrasena, int deuda) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.deuda = deuda;
    }

    /**
     * Metodos get y set de usuario, constraseña y deuda
     * @return usuario, constraseña y deuda
     */
    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public int getDeuda() {
        return deuda;
    }

    public void setDeuda(int deuda) {
        this.deuda = deuda;
    }
}
