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
    private JTextField searchField;

    public CountriesPanel() {
        setLayout(new BorderLayout(10, 10));
        initTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
        loadData();
    }

    private void initTable() {
        table = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Название страны", "Население"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Поля ввода
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        idField = new JTextField(6);
        nameField = new JTextField(20);
        populationField = new JTextField(10);

        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Название страны:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Население:"));
        inputPanel.add(populationField);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addBtn = new JButton("Добавить страну");
        JButton deleteBtn = new JButton("Удалить выбранную");

        addBtn.addActionListener(e -> addCountry());
        deleteBtn.addActionListener(e -> deleteCountry());

        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);

        // Поиск
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTable(searchField.getText().trim());
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTable(searchField.getText().trim());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTable(searchField.getText().trim());
            }
        });

        searchPanel.add(new JLabel("Поиск:"));
        searchPanel.add(searchField);

        panel.add(inputPanel);
        panel.add(buttonPanel);
        panel.add(searchPanel);
        return panel;
    }

    private void filterTable(String searchText) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {
            DatabaseManager.getAllCountries().stream()
                    .filter(country -> country.getName().toLowerCase().contains(searchText.toLowerCase()))
                    .forEach(country -> model.addRow(new Object[]{
                            country.getId(),
                            country.getName(),
                            String.format("%,d", country.getPopulation())
                    }));
        } catch (SQLException ex) {
            handlePostgreSQLError(ex);
        }
    }

    private void addCountry() {
        try {
            String idStr = idField.getText().trim();
            String name = nameField.getText().trim();
            String populationStr = populationField.getText().trim();

            if (!idStr.matches("\\d+")) {
                throw new IllegalArgumentException("Некорректный формат числа.");
            }

            if (!name.matches("[a-zA-Z\\s]+")) {
                throw new IllegalArgumentException("Некорректное название страны.");
            }

            if (name.isEmpty()) {
                throw new IllegalArgumentException("Название страны не может быть пустым.");
            }

            if (!populationStr.matches("\\d+")) {
                throw new IllegalArgumentException("Некорректный формат числа.");
            }

            int id = Integer.parseInt(idStr);
            int population = Integer.parseInt(populationStr);

            Country country = new Country(id, name, population);
            DatabaseManager.addCountry(country);
            loadData();
            clearFields();

        } catch (NumberFormatException ex) {
            showError("Некорректный формат числа.");
        } catch (SQLException ex) {
            handlePostgreSQLError(ex);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteCountry() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Выберите страну для удаления.");
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
        if (ex.getSQLState().startsWith("23")) {
            errorMessage = "Ошибка: нарушение ограничений базы данных.";
        } else {
            errorMessage = "Ошибка базы данных: " + ex.getMessage();
        }
        showError(errorMessage);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}