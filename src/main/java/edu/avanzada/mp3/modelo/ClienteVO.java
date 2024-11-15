package edu.avanzada.mp3.modelo;

public class ClienteVO {

    private String usuario;
    private String contrasena;
    private int deuda;

    public ClienteVO(String usuario, String contrasena, int deuda) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.deuda = deuda;
    }

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
