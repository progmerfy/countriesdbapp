// CitiesPanel.java
package gui;

import dao.DatabaseManager;
import model.City;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class CitiesPanel extends JPanel {
    private JTable table;
    private JTextField nameField;
    private JTextField populationField;
    private JTextField countryIdField;
    private JTextField searchField;

    public CitiesPanel() {
        setLayout(new BorderLayout(10, 10));
        initTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
        loadData();
    }

    private void initTable() {
        table = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Название города", "Население", "ID страны"}, 0) {
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
        nameField = new JTextField(20);
        populationField = new JTextField(10);
        countryIdField = new JTextField(6);

        inputPanel.add(new JLabel("Название города:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Население:"));
        inputPanel.add(populationField);
        inputPanel.add(new JLabel("ID страны:"));
        inputPanel.add(countryIdField);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addBtn = new JButton("Добавить город");
        JButton deleteBtn = new JButton("Удалить выбранный");

        addBtn.addActionListener(e -> addCity());
        deleteBtn.addActionListener(e -> deleteCity());

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
            DatabaseManager.getAllCities().stream()
                    .filter(city -> city.getName().toLowerCase().contains(searchText.toLowerCase()))
                    .forEach(city -> model.addRow(new Object[]{
                            city.getId(),
                            city.getName(),
                            String.format("%,d", city.getPopulation()),
                            city.getCountryId()
                    }));
        } catch (SQLException ex) {
            handlePostgreSQLError(ex);
        }
    }

    private void addCity() {
        try {
            String name = nameField.getText().trim();
            String populationStr = populationField.getText().trim();
            String countryIdStr = countryIdField.getText().trim();

            if (!name.matches("[a-zA-Z\\s]+")) {
                throw new IllegalArgumentException("Некорректное название города.");
            }

            if (name.isEmpty()) {
                throw new IllegalArgumentException("Название города не может быть пустым.");
            }

            if (!DatabaseManager.countryExists(Integer.parseInt(countryIdStr))) {
                throw new IllegalArgumentException("ID страны не существует.");
            }

            if (!populationStr.matches("\\d+") || !countryIdStr.matches("\\d+")) {
                throw new IllegalArgumentException("Некорректный формат числа.");
            }

            int population = Integer.parseInt(populationStr);
            int countryId = Integer.parseInt(countryIdStr);

            City city = new City(0, name, population, countryId);
            DatabaseManager.addCity(city);
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

    private void deleteCity() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Выберите город для удаления.");
            return;
        }

        int cityId = (int) table.getValueAt(selectedRow, 0);
        try {
            DatabaseManager.deleteCity(cityId);
            loadData();
        } catch (SQLException ex) {
            handlePostgreSQLError(ex);
        }
    }

    private void loadData() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {
            DatabaseManager.getAllCities().forEach(city ->
                    model.addRow(new Object[]{
                            city.getId(),
                            city.getName(),
                            String.format("%,d", city.getPopulation()),
                            city.getCountryId()
                    })
            );
        } catch (SQLException ex) {
            handlePostgreSQLError(ex);
        }
    }

    private void clearFields() {
        nameField.setText("");
        populationField.setText("");
        countryIdField.setText("");
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