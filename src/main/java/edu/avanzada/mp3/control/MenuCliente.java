package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.CancionDAO;
import edu.avanzada.mp3.modelo.ClienteDAO;
import edu.avanzada.mp3.vista.ControladorVentana;
import edu.avanzada.mp3.vista.VistaPagar;
import edu.avanzada.mp3.vista.VistaReproductor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuCliente implements ActionListener {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String nombreUsuario;
    private ControladorVentana mensajes;
    private ClienteDAO clienteDAO;
    private VistaReproductor vistaR;
    private VistaPagar recibo;
    private String costo;
    

    public MenuCliente(Socket socket, ObjectOutputStream out, ObjectInputStream in, String nombreUsuario, ControladorVentana mensajes, ClienteDAO clienteDAO) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.nombreUsuario = nombreUsuario;
        this.mensajes = mensajes;
        this.clienteDAO = clienteDAO;
    }

    public void mostrarCanciones() {
        vistaR = new VistaReproductor(this);
        vistaR.actualizar.addActionListener(this);
        vistaR.salir.addActionListener(this);
        vistaR.jTable1.removeAll();
        try {

            int numeroCanciones = in.readInt(); // Leer el número de canciones
            System.out.println(numeroCanciones);

            System.out.println("Número de canciones recibidas: " + numeroCanciones);
            if (numeroCanciones == 0) {
                System.out.println("No hay canciones disponibles.");
            } else {
                for (int i = 0; i < numeroCanciones; i++) {
                    CancionDAO cancion2 = new CancionDAO();
                    String cancion = in.readUTF();// Recibe el nombre de cada canción
                    String artista = cancion2.encontrarartistaCancion(i);
                    vistaR.TablaDinamico(cancion, artista);
                }
            }
            // Leer el mensaje final de "fin_lista"
            String finLista = "" + in.readUTF();
            if ("fin_lista".equals(finLista)) {
                System.out.println("Lista de canciones recibida correctamente.");
            } else {
                System.out.println("Error: no se recibió el final esperado de la lista.");
            }
        } catch (IOException e) {
        }
    }

    public void seleccionarYDescargarCancion(int numeroCancion) {
        try {
            int deuda = clienteDAO.verificarDeuda(nombreUsuario);
            clienteDAO.actualizarEstadoPago(nombreUsuario, deuda + 15000);
        } catch (SQLException ex) {
            Logger.getLogger(MenuCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            // Enviar la opción al servidor
            out.writeObject(String.valueOf(numeroCancion));

            // Recibir el nombre de la canción o mensajes de error
            String respuesta = (String) in.readObject();

            if (respuesta.startsWith("error_")) {
                mensajes.mostrarMensajeSystem("Error: " + respuesta.substring(6).replace('_', ' '));
                return;
            }

            // Es un nombre de canción válido, proceder con la descarga
            String nombreCancion = respuesta;
            String nombreArchivo = nombreCancion + "_" + this.nombreUsuario + ".mp3";
            String rutaDestino = "descargas/" + nombreArchivo;

            // Asegurarse de que el directorio de descargas existe
            new File("descargas").mkdirs();

            // Preparar para recibir el archivo
            try (FileOutputStream fos = new FileOutputStream(rutaDestino); BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                byte[] buffer = new byte[4096];
                int bytesLeidos;

                mensajes.mostrarMensajeSystem("Iniciando descarga de " + nombreCancion);
                mensajes.mostrarMensajeSystem("Se guardará como: " + nombreArchivo);

                // Recibir el archivo sin conocer el tamaño previamente
                while ((bytesLeidos = in.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesLeidos);
                }

                mensajes.mostrarMensajeSystem("\nDescarga completada: " + rutaDestino);
            }

            // Esperar el mensaje de confirmación
            String confirmacion = (String) in.readObject();
            if ("descarga_completada".equals(confirmacion)) {
                mensajes.mostrarMensajeSystem("La descarga ha finalizado correctamente.");
            } else {
                mensajes.mostrarMensajeSystem("Error: No se recibió la confirmación esperada.");
            }

        } catch (EOFException e) {
            mensajes.mostrarMensaje("Error: Se alcanzó el final del flujo de datos inesperadamente.");
        } catch (IOException | ClassNotFoundException e) {
            mensajes.mostrarMensajeSystem("Error durante la descarga: " + e.getMessage());
        }
    }

    private void solicitarActualizarCanciones() {
        try {
            out.writeObject("actualizar");
            out.flush();
            mensajes.mostrarMensajeSystem("Solicitando actualización de la lista de canciones...");
        } catch (IOException e) {
            mensajes.mostrarError("Error al enviar solicitud de actualización: " + e.getMessage());
        }
    }

    public void Pagar() {
        recibo = new VistaPagar();
        recibo.pagar.addActionListener(this);
        try {
            costo = "" + clienteDAO.verificarDeuda(nombreUsuario);
        } catch (Exception e) {
        }
        String rec = "Cliente: " + this.nombreUsuario + "<p>"
                + "<p>Valor secion: " + costo;
        recibo.recibo.setText("<html>" + rec + "<html>");
    }

    // Método para cerrar la conexión de red y liberar recursos
    private void cerrarConexion() {
        try {
            out.writeObject("logout");
            out.flush();
            String costoFinal = (String) in.readObject();
            String mensajeFinal = (String) in.readObject();
            mensajes.mostrarMensajeSystem(costoFinal);
            mensajes.mostrarMensajeSystem(mensajeFinal);

            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
            mensajes.mostrarMensajeSystem("Conexión de cliente cerrada.");
            System.exit(0);
        } catch (IOException | ClassNotFoundException e) {
            mensajes.mostrarMensajeSystem("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if ("Actualizar".equals(e.getActionCommand())) {
            vistaR.dispose();
            solicitarActualizarCanciones();
            mostrarCanciones();
        }
        
        if ("Salir".equals(e.getActionCommand())) {
            solicitarActualizarCanciones();
            vistaR.dispose();
            Pagar();
        }
        
        if ("Pagar".equals(e.getActionCommand())) {
            try {
                out.write(0);
                clienteDAO.actualizarEstadoPago(nombreUsuario, 0);
            } catch (SQLException ex) {
                Logger.getLogger(MenuCliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MenuCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            solicitarActualizarCanciones();
        }
        
        if ("Okey".equals(e.getActionCommand())) {
            recibo.dispose();
            cerrarConexion();
        }
    }
}
