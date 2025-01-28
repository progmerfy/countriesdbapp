// CountriesPanel.java
package gui;

import dao.DatabaseManager;
import model.Country;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class CountriesPanel extends JPanel {
    private JTable table;
    private JTextField idField;
    private JTextField nameField;
    private JTextField populationField;

    public CountriesPanel() {
        setLayout(new BorderLayout(10, 10));
        initTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
        loadData();
    }

    private void initTable() {
        table = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Country Name", "Population"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input fields
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        idField = new JTextField(6);
        nameField = new JTextField(20);
        populationField = new JTextField(10);

        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Population:"));
        inputPanel.add(populationField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addBtn = new JButton("Add Country");
        JButton deleteBtn = new JButton("Delete Selected");

        addBtn.addActionListener(e -> addCountry());
        deleteBtn.addActionListener(e -> deleteCountry());

        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);

        panel.add(inputPanel);
        panel.add(buttonPanel);
        return panel;
    }

    private void addCountry() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            int population = Integer.parseInt(populationField.getText().trim());

            if (name.isEmpty()) {
                throw new IllegalArgumentException("Country name cannot be empty");
            }

            Country country = new Country(id, name, population);
            DatabaseManager.addCountry(country);
            loadData();
            clearFields();

        } catch (NumberFormatException ex) {
            showError("Invalid number format in ID or Population");
        } catch (SQLException ex) {
            handlePostgreSQLError(ex);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteCountry() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a country to delete");
            return;
        }

        int countryId = (int) table.getValueAt(selectedRow, 0);
        try {
            DatabaseManager.deleteCountry(countryId);
            loadData();
        } catch (SQLException ex) {
            handlePostgreSQLError(ex);
        }
    }

    private void loadData() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {
            DatabaseManager.getAllCountries().forEach(country ->
                    model.addRow(new Object[]{
                            country.getId(),
                            country.getName(),
                            String.format("%,d", country.getPopulation())
                    })
            );
        } catch (SQLException ex) {
            handlePostgreSQLError(ex);
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        populationField.setText("");
    }

    private void handlePostgreSQLError(SQLException ex) {
        String errorMessage;
        if (ex.getSQLState().startsWith("23")) { // Integrity constraint violation
            errorMessage = "Cannot delete country: referenced by other records";
        } else {
            errorMessage = "Database error: " + ex.getMessage();
        }
        showError(errorMessage);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}