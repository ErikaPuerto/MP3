package edu.avanzada.mp3.vista;

import edu.avanzada.mp3.control.AcVista;
import edu.avanzada.mp3.control.MenuCliente;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author anaro
 */
public class VistaReproductor extends javax.swing.JFrame {

    /**
     * Creates new form VistaLogin
     */
    public static int columna, row;
    DefaultTableModel modelotabla = new DefaultTableModel();
    javax.swing.JButton boton1 = new JButton();
    javax.swing.JButton boton2 = new JButton();
    javax.swing.JButton boton3;
    private MenuCliente Gestor;

    /**
     * Constructor de la clase VistaReproductor. Inicializa los componentes, 
     * configura la ventana y la tabla, y ajusta la visibilidad de la misma.
     * 
     * @param Gestor Objeto que gestiona las interacciones con el menú cliente.
     */
    public VistaReproductor(MenuCliente Gestor) {
        this.Gestor = Gestor;
        initComponents();
        MostrarTabla();
        inicio();
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * Método que inicializa el aspecto visual y comportamiento de los controles
     * de la interfaz, como los botones y las propiedades de la tabla.
     */
    public void inicio() {
        boton1.setContentAreaFilled(false);
        boton2.setContentAreaFilled(false);
        boton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/dowland.png")));
        boton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/play.png")));
        jTable1.getTableHeader().setReorderingAllowed(false);
        TableColumn column1 = jTable1.getColumnModel().getColumn(0);
        column1.setPreferredWidth(300); // Ancho para la columna "Nombre"
        TableColumn column2 = jTable1.getColumnModel().getColumn(1);
        column2.setPreferredWidth(1);  // Ancho para la columna "Edad"      
        TableColumn column3 = jTable1.getColumnModel().getColumn(2);
        column3.setPreferredWidth(1); // Ancho para la columna "País"
        jTable1.setRowHeight(53);
        jTable1.getTableHeader().setVisible(false);

    }

    /**
     * Método para configurar el botón de la interfaz que representa una canción.
     */
    public void Cb() {
        boton3 = new JButton();
        boton3.setBorderPainted(false);
        boton3.setContentAreaFilled(false);
        boton3.setFocusPainted(false);
        boton3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        boton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/ranita.png")));
    }

    /**
     * Método generado automáticamente para inicializar los componentes de la interfaz gráfica.
     * Este método es generado por el editor de formularios y no debe ser modificado manualmente.
     */
    @SuppressWarnings("unchecked")
    
    /**
     * Método que se llama cuando se hace clic en una celda de la tabla de canciones.
     * Realiza acciones basadas en la columna y fila seleccionada, como descargar
     * o reproducir una canción.
     * 
     * @param evt Evento de clic en la tabla.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPanel1 = new FondoPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        salir = new javax.swing.JButton();
        actualizar = new javax.swing.JButton();
        next = new javax.swing.JButton();
        play = new javax.swing.JButton();
        previous = new javax.swing.JButton();
        nowsingle = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(221, 249, 212));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoscrolls(false);
        jTable1.setEnabled(false);
        jTable1.setFocusable(false);
        jTable1.setRequestFocusEnabled(false);
        jTable1.setSelectionForeground(new java.awt.Color(255, 102, 204));
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setResizingAllowed(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
        }

        jLabel1.setFont(new java.awt.Font("Goudy Old Style", 0, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Musica disponible");

        salir.setText("Salir");

        actualizar.setText("Actualizar");

        next.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconfinder_next_293690.png"))); // NOI18N
        next.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextActionPerformed(evt);
            }
        });

        play.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconfinder_play-arrow_326577_1.png"))); // NOI18N
        play.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playActionPerformed(evt);
            }
        });

        previous.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconfinder_skip-previous_326509.png"))); // NOI18N
        previous.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousActionPerformed(evt);
            }
        });

        nowsingle.setText(" ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(46, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(nowsingle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(previous, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(play, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(next, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(salir, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(salir)
                            .addComponent(actualizar)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(next, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(play, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(previous, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nowsingle, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        columna = jTable1.getColumnModel().getColumnIndexAtX(evt.getX());
        row = evt.getY() / jTable1.getRowHeight();
        if (columna == 1) {
            Gestor.seleccionarYDescargarCancion(row + 1);
        }
        if (columna == 2) {
            Gestor.reproducirCancion(row);
        }

    }//GEN-LAST:event_jTable1MouseClicked
    /**
     * Método que se llama cuando se hace clic en el botón de "play".
     * Ejecuta la acción de reproducir la canción actual.
     * @param evt Evento de clic en el botón de reproducción.
     */
    private void playActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playActionPerformed
        Gestor.play();
    }//GEN-LAST:event_playActionPerformed

    /**
     * Método que se llama cuando se hace clic en el botón de "next".
     * Ejecuta la acción de pasar a la siguiente canción.
     * @param evt Evento de clic en el botón de siguiente.
     */
    private void nextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextActionPerformed
        Gestor.next();
    }//GEN-LAST:event_nextActionPerformed
/**
     * Método que se llama cuando se hace clic en el botón de "previous".
     * Ejecuta la acción de retroceder a la canción anterior.
     * @param evt Evento de clic en el botón de anterior.
     */
    private void previousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousActionPerformed
        Gestor.previous();
                
    }//GEN-LAST:event_previousActionPerformed
    
    /**
     * Método para mostrar la tabla de canciones en la interfaz.
     */
    public void MostrarTabla() {
        jTable1.setDefaultRenderer(Object.class, new AcVista());
        modelotabla.setColumnIdentifiers(new String[]{" ",
            " ", " "});
        jTable1.setModel(modelotabla);
        jTable1.setRowHeight(20);
    }

    /**
     * Método para agregar una fila dinámica en la tabla de canciones.
     * @param s Nombre de la canción.
     * @param p País de origen del artista.
     */
    public void TablaDinamico(String s, String p) {
        String b = s + "<p>" + p;
        Cb();
        boton3.setText("<html>" + b + "<html>");
        Object struct[] = {boton3, boton1, boton2};
        modelotabla.addRow(struct);

    }
    /**
     * Clase interna que extiende JPanel y permite dibujar una imagen de fondo
     * en la ventana del reproductor.
     */
    class FondoPanel extends JPanel {

        private Image imagen;

        @Override

        public void paint(Graphics g) {
            imagen = new ImageIcon(getClass().getResource("/imagenes/fondo.png")).getImage();
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
            setOpaque(false);
            super.paint(g);
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton actualizar;
    public javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTable jTable1;
    public javax.swing.JButton next;
    public javax.swing.JLabel nowsingle;
    public javax.swing.JButton play;
    public javax.swing.JButton previous;
    public javax.swing.JButton salir;
    // End of variables declaration//GEN-END:variables
}
