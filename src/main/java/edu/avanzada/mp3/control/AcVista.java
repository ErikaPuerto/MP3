package edu.avanzada.mp3.control;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class AcVista extends DefaultTableCellRenderer {


    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
        if (o instanceof JButton) {
            JButton boton = (JButton) o;
            return boton;
        }
        return super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
    }

}
