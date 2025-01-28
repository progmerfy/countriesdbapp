package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TableHelper {
    public static JTable createTable(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return table;
    }
}