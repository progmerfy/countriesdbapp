// LanguagesPanel.java
package gui;

import dao.DatabaseManager;
import model.Language;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class LanguagesPanel extends JPanel {
    private JTable table;
    private JTextField nameField;
    private JTextField countryIdField;

    public LanguagesPanel() {
        setLayout(new BorderLayout(10, 10));
        initTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
        loadData();
    }

    private void initTable() {
        table = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Language", "Country ID"}, 0) {
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
        nameField = new JTextField(20);
        countryIdField = new JTextField(6);

        inputPanel.add(new JLabel("Language:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Country ID:"));
        inputPanel.add(countryIdField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addBtn = new JButton("Add Language");
        JButton deleteBtn = new JButton("Delete Selected");

        addBtn.addActionListener(e -> addLanguage());
        deleteBtn.addActionListener(e -> deleteLanguage());

        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);

        panel.add(inputPanel);
        panel.add(buttonPanel);
        return panel;
    }

    private void addLanguage() {
        try {
            String name = nameField.getText().trim();
            int countryId = Integer.parseInt(countryIdField.getText().trim());

            if (name.isEmpty()) {
                throw new IllegalArgumentException("Language name cannot be empty");
            }

            if (!DatabaseManager.countryExists(countryId)) {
                throw new IllegalArgumentException("Country ID does not exist");
            }

            Language language = new Language(0, name, countryId);
            DatabaseManager.addLanguage(language);
            loadData();
            clearFields();

        } catch (NumberFormatException ex) {
            showError("Invalid number format in Country ID");
        } catch (SQLException ex) {
            handlePostgreSQLError(ex);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteLanguage() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a language to delete");
            return;
        }

        int languageId = (int) table.getValueAt(selectedRow, 0);
        try {
            DatabaseManager.deleteLanguage(languageId);
            loadData();
        } catch (SQLException ex) {
            handlePostgreSQLError(ex);
        }
    }

    private void loadData() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {
            DatabaseManager.getAllLanguages().forEach(language ->
                    model.addRow(new Object[]{
                            language.getId(),
                            language.getName(),
                            language.getCountryId()
                    })
            );
        } catch (SQLException ex) {
            handlePostgreSQLError(ex);
        }
    }

    private void clearFields() {
        nameField.setText("");
        countryIdField.setText("");
    }

    private void handlePostgreSQLError(SQLException ex) {
        String errorMessage;
        if (ex.getSQLState().startsWith("23")) {
            errorMessage = "Operation failed: constraint violation";
        } else {
            errorMessage = "Database error: " + ex.getMessage();
        }
        showError(errorMessage);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}