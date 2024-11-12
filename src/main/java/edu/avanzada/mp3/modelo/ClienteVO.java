package edu.avanzada.mp3.modelo;

public class ClienteVO {
    private String usuario;
    private String contrasena;
    private double deuda;

    public ClienteVO(String usuario, String contrasena, double deuda) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.deuda = deuda;
    }

    public String getUsuario() { return usuario; }
    public String getContrasena() { return contrasena; }
    public double getDeuda() { return deuda; }
    public void setDeuda(double deuda) { this.deuda = deuda; }
}

