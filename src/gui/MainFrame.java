// MainFrame.java
package gui;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("PostgreSQL Country Manager");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Countries", new CountriesPanel());
        tabbedPane.addTab("Cities", new CitiesPanel());
        tabbedPane.addTab("Languages", new LanguagesPanel());

        add(tabbedPane);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}