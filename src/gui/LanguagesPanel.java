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
    private JTextField searchField;

    public LanguagesPanel() {
        setLayout(new BorderLayout(10, 10));
        initTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
        loadData();
    }

    private void initTable() {
        table = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Название языка", "ID страны"}, 0) {
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
        countryIdField = new JTextField(6);

        inputPanel.add(new JLabel("Название языка:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("ID страны:"));
        inputPanel.add(countryIdField);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addBtn = new JButton("Добавить язык");
        JButton deleteBtn = new JButton("Удалить выбранный");

        addBtn.addActionListener(e -> addLanguage());
        deleteBtn.addActionListener(e -> deleteLanguage());

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
            DatabaseManager.getAllLanguages().stream()
                    .filter(language -> language.getName().toLowerCase().contains(searchText.toLowerCase()))
                    .forEach(language -> model.addRow(new Object[]{
                            language.getId(),
                            language.getName(),
                            language.getCountryId()
                    }));
        } catch (SQLException ex) {
            handlePostgreSQLError(ex);
        }
    }

    private void addLanguage() {
        try {
            String name = nameField.getText().trim();
            String countryIdStr = countryIdField.getText().trim();

            if (!name.matches("[a-zA-Z\\s]+")) {
                throw new IllegalArgumentException("Некорректное название языка.");
            }

            if (name.isEmpty()) {
                throw new IllegalArgumentException("Название языка не может быть пустым.");
            }

            if (!countryIdStr.matches("\\d+")) {
                throw new IllegalArgumentException("Некорректный формат числа.");
            }

            int countryId = Integer.parseInt(countryIdStr);

            if (!DatabaseManager.countryExists(countryId)) {
                throw new IllegalArgumentException("ID страны не существует.");
            }

            Language language = new Language(0, name, countryId);
            DatabaseManager.addLanguage(language);
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

    private void deleteLanguage() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Выберите язык для удаления.");
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