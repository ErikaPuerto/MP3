package edu.avanzada.mp3.control;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderizador de celdas para {@link JTable} que permite mostrar botones.
 */
public class AcVista extends DefaultTableCellRenderer {

    /**
     * Renderiza un {@link JButton} en una celda de la tabla si el valor es un botón.
     *
     * @param jtable la tabla {@link JTable}
     * @param o el valor de la celda, que puede ser un {@link JButton} u otro objeto
     * @param bln {@code true} si la celda está seleccionada
     * @param bln1 {@code true} si la celda tiene el foco
     * @param i el índice de la fila
     * @param i1 el índice de la columna
     * @return el componente que renderiza la celda
     */
    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
        if (o instanceof JButton) {
            return (JButton) o;
        }
        return super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
    }
}
