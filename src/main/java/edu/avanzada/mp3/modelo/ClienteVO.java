package edu.avanzada.mp3.modelo;

public class ClienteVO {
    private int id;
    private String usuario;
    private String contrasena;
    private double estadoPago;

    public ClienteVO(int id, String usuario, String contrasena, double estadoPago) {
        this.id = id;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.estadoPago = estadoPago;
    }

    

    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getContrasena() { return contrasena; }
    public double getEstadoPago() { return estadoPago; }
    public void setEstadoPago(double estadoPago) { this.estadoPago = estadoPago; }
}

