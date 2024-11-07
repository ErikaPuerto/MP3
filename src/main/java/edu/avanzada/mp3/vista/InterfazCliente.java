package edu.avanzada.mp3.vista;

import javax.swing.*;

public class InterfazCliente extends JFrame {
    private JList<String> listaCanciones;
    private JButton btnDescargar, btnReproducir, btnLogout;
    private DefaultListModel<String> modeloLista;

    public InterfazCliente() {
        modeloLista = new DefaultListModel<>();
        listaCanciones = new JList<>(modeloLista);
        
        btnDescargar = new JButton("Descargar");
        btnDescargar.addActionListener(e -> descargarCancion());

        btnReproducir = new JButton("Reproducir");
        btnReproducir.addActionListener(e -> reproducirCancion());

        btnLogout = new JButton("Salir");
        btnLogout.addActionListener(e -> salir());

        add(new JScrollPane(listaCanciones));
        add(btnDescargar);
        add(btnReproducir);
        add(btnLogout);
    }

    private void descargarCancion() {
        // Lógica para solicitar descarga de la canción seleccionada
    }

    private void reproducirCancion() {
        // Lógica para reproducir canción usando JMF
    }

    private void salir() {
        // Enviar logout al servidor y cerrar la ventana
    }
}
