package edu.avanzada.mp3.control;

import edu.avanzada.mp3.modelo.Cancion;
import edu.avanzada.mp3.modelo.CancionDAO;
import edu.avanzada.mp3.modelo.ClienteDAO;
import edu.avanzada.mp3.modelo.IMensajeria;
import edu.avanzada.mp3.vista.ControladorVentana;
import edu.avanzada.mp3.vista.VistaPagar;
import edu.avanzada.mp3.vista.VistaReproductor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import jaco.mp3.player.MP3Player;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que representa el menú del cliente en una aplicación de reproducción de música.
 * Gestiona la conexión con el servidor y el control de reproducción de canciones.
 */
public class MenuCliente implements ActionListener {

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final String nombreUsuario;
    private final ControladorVentana mensajes;
    private final IMensajeria mensajeria;
    private final ClienteDAO clienteDAO;
    private VistaReproductor vistaR;
    private VistaPagar recibo;
    private String costo;
    private int cd;
    private int numCancion;
    private MP3Player player;
    private Map<Integer, Cancion> cancionesDescargadas = new HashMap<>();
    private int numCancionActual;
    private List<Integer> playlistCanciones;
    private ArrayList songs;
    private String nomCancion;

    /**
     * Constructor de MenuCliente.
     * @param socket Socket para la conexión.
     * @param out ObjectOutputStream para enviar datos.
     * @param in ObjectInputStream para recibir datos.
     * @param nombreUsuario Nombre del usuario.
     * @param mensajes Controlador para mostrar mensajes.
     * @param clienteDAO ClienteDAO para operaciones con clientes.
     */
    public MenuCliente(Socket socket, ObjectOutputStream out, ObjectInputStream in, String nombreUsuario, ControladorVentana mensajes, ClienteDAO clienteDAO) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.nombreUsuario = nombreUsuario;
        this.mensajes = mensajes;
        this.mensajeria = new AdaptadorMensajeria(mensajes);
        this.clienteDAO = clienteDAO;
    }
    
    /**
     * Inicializa la lista de reproducción a partir de las canciones descargadas.
     */
    private void inicializarPlaylist() {
        playlistCanciones = new ArrayList<>();
        for (Integer numCancion : cancionesDescargadas.keySet()) {
            playlistCanciones.add(numCancion);
        }
        // Ordenar la lista para tener un orden consistente
        Collections.sort(playlistCanciones);
        
        // Establecer la canción actual como la primera de la lista
        if (!playlistCanciones.isEmpty()) {
            numCancionActual = playlistCanciones.get(0);
        }
    }

    /**
     * Reproduce una canción específica.
     * @param numCancion Número de la canción a reproducir.
     */
    public void reproducirCancion(int numCancion) {
        // Validar que haya canciones descargadas
        if (cancionesDescargadas.isEmpty()) {
            mensajes.mostrarMensaje("No hay canciones descargadas para reproducir.");
            return;
        }
        
        // Verificar si la canción existe en el mapa
        if (!cancionesDescargadas.containsKey(numCancion)) {
            mensajes.mostrarMensaje("La cancion seleccionada no ha sido descargada.");
            return;
        }
        
        // Ajustar el índice para estar dentro del rango válido
        this.numCancion = numCancion % cancionesDescargadas.size();

        // Obtener la canción descargada desde la lista
        Cancion cancion = cancionesDescargadas.get(numCancion);

        // Reiniciar el reproductor si ya estaba reproduciendo algo
        cd = 1;
        if (player != null) {
            player.stop();
        }

        // Configurar el reproductor con la ruta de la canción descargada
        player = new MP3Player(new File(cancion.getUbicacion()));
        player.play();

        // Actualizar el nombre de la canción actual
        nomCancion = cancion.getNombre();
        mensajes.mostrarMensajeSystem("Reproduciendo: " + nomCancion);

        // Actualizar la etiqueta en la vista, si es necesario
        actualizarLabel();
        numCancionActual = numCancion;
    }

    /**
     * Método que alterna entre pausar y reanudar la reproducción.
     */
    public void play() {
        if (player != null) {
            switch (cd) {
                case 1 -> {
                    player.pause();
                    cd = 2;
                }
                case 2 -> {
                    player.play();
                    cd = 1;
                }
                default -> throw new AssertionError();
            }
        }
    }

    /**
     * Método que reproduce la siguiente canción en la lista de reproducción.
     */
    public void next() {
        // Inicializar playlist si aún no se ha hecho
        if (playlistCanciones == null) {
            inicializarPlaylist();
        }

        // Si no hay canciones, no hacer nada
        if (playlistCanciones.isEmpty()) {
            mensajes.mostrarMensaje("No hay canciones disponibles.");
            return;
        }

        // Encontrar el índice de la canción actual en la playlist
        int indiceActual = playlistCanciones.indexOf(numCancionActual);
        
        // Pasar a la siguiente canción
        int siguienteIndice = (indiceActual + 1) % playlistCanciones.size();
        int siguienteNumCancion = playlistCanciones.get(siguienteIndice);
        
        reproducirCancion(siguienteNumCancion);
    }

    /**
     * Método que reproduce la canción anterior en la lista de reproducción.
     */
    public void previous() {
        // Inicializar playlist si aún no se ha hecho
        if (playlistCanciones == null) {
            inicializarPlaylist();
        }

        // Si no hay canciones, no hacer nada
        if (playlistCanciones.isEmpty()) {
            mensajes.mostrarMensaje("No hay canciones disponibles.");
            return;
        }

        // Encontrar el índice de la canción actual en la playlist
        int indiceActual = playlistCanciones.indexOf(numCancionActual);
        
        // Pasar a la canción anterior
        int anteriorIndice = (indiceActual - 1 + playlistCanciones.size()) % playlistCanciones.size();
        int anteriorNumCancion = playlistCanciones.get(anteriorIndice);
        
        reproducirCancion(anteriorNumCancion);
    }

    /**
     * Metodo que muestra la lista de canciones disponibles en la interfaz.
     */
    public void mostrarCanciones() {
        vistaR = new VistaReproductor(this);
        vistaR.actualizar.addActionListener(this);
        vistaR.salir.addActionListener(this);
        vistaR.jTable1.removeAll();
        songs = new ArrayList();
        try {

            int numeroCanciones = in.readInt(); // Leer el número de canciones
            System.out.println(numeroCanciones);

            System.out.println("Numero de canciones recibidas: " + numeroCanciones);
            if (numeroCanciones == 0) {
                System.out.println("No hay canciones disponibles.");
            } else {
                for (int i = 0; i < numeroCanciones; i++) {
                    CancionDAO cancion2 = new CancionDAO(mensajeria);
                    String cancion = in.readUTF();// Recibe el nombre de cada canción
                    File songFile;
                    try {
                        //songFile = new File("C:\\Users\\anaro\\OneDrive\\Documentos\\MP3f\\MP3\\src\\main\\resources\\Musica\\" + cancion + ".mp3").getAbsoluteFile();
                        songFile = new File("src/main/resources/Musica/" + cancion + ".mp3").getAbsoluteFile();
                        songs.add(songFile);
                    } catch (Exception e) {
                    }
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

    /**
     * Selecciona y descarga una canción específica.
     * @param numeroCancion Número de la canción a descargar.
     */
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
                mensajes.mostrarMensajeSystem("Se guardara como: " + nombreArchivo);

                // Recibir el archivo sin conocer el tamaño previamente
                while ((bytesLeidos = in.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesLeidos);
                }
                
                // Agregar a la lista de canciones descargadas
                Cancion cancionDescargada = new Cancion(nombreCancion, "Artista desconocido", rutaDestino);
                int numCan = numeroCancion - 1;
                cancionesDescargadas.put(numCan, cancionDescargada);
                inicializarPlaylist();

                mensajes.mostrarMensajeSystem("\nDescarga completada: " + rutaDestino);
            }

            // Esperar el mensaje de confirmación
            String confirmacion = (String) in.readObject();
            if ("descarga_completada".equals(confirmacion)) {
                mensajes.mostrarMensajeSystem("La descarga ha finalizado correctamente.");
            } else {
                mensajes.mostrarMensajeSystem("Error: No se recibio la confirmacion esperada.");
            }

        } catch (EOFException e) {
            mensajes.mostrarMensaje("Error: Se alcanzo el final del flujo de datos inesperadamente.");
        } catch (IOException | ClassNotFoundException e) {
            mensajes.mostrarMensajeSystem("Error durante la descarga: " + e.getMessage());
        }
    }

    /**
     * Solicita la actualización de la lista de canciones en el servidor.
     */
    private void solicitarActualizarCanciones() {
        try {
            out.writeObject("actualizar");
            out.flush();
            mensajes.mostrarMensajeSystem("Solicitando actualización de la lista de canciones...");
        } catch (IOException e) {
            mensajes.mostrarError("Error al enviar solicitud de actualizacion: " + e.getMessage());
        }
    }

    /**
     * Muestra la interfaz de pago de deudas del cliente.
     */
    public void Pagar() {
        recibo = new VistaPagar();
        recibo.pagar.addActionListener(this);
        try {
            costo = "" + clienteDAO.verificarDeuda(nombreUsuario);
        } catch (SQLException e) {
        }
        String rec = "Cliente: " + this.nombreUsuario + "<p>"
                + "<p>Valor sesion: " + costo;
        recibo.recibo.setText("<html>" + rec + "<html>");
    }

    /**
     * Actualiza el nombre de la canción actual en la interfaz.
     */
    public void actualizarLabel() {
        String nombre = "";
        for (int i = 70; i < nomCancion.length(); i++) {
            nombre += nomCancion.charAt(i);
        }
        vistaR.nowsingle.setText(nombre);
    }

    /** Método para cerrar la conexión de red y liberar recursos*/
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
            mensajes.mostrarMensajeSystem("Conexion de cliente cerrada.");
            System.exit(0);
        } catch (IOException | ClassNotFoundException e) {
            mensajes.mostrarMensajeSystem("Error al cerrar la conexion: " + e.getMessage());
        }
    }

    /**
     * Maneja eventos de la interfaz de usuario.
     * @param e Evento de acción.
     */
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
        if ("Okey".equals(e.getActionCommand())) {
            recibo.dispose();
            cerrarConexion();
        }
    }
}
