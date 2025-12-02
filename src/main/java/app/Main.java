package app;
import view.DataSetTableView;

import javax.swing.*;
import java.awt.*;

public class  Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Set modern dark theme
            setupModernDarkTheme();
            
            DataAnalysisAppBuilder appBuilder = new DataAnalysisAppBuilder();

            JFrame application = appBuilder
                    .addDataSetTableView()
                    .addSearchUseCase()
                    .addTableDisplayUseCase()
                    .addVisualizationUseCase()
                    .loadSampleData()
                    .build();

            application.setSize(1400, 900);
            application.setLocationRelativeTo(null);
            application.setVisible(true);

            DataSetTableView tableView = appBuilder.getDataSetTableView();
            tableView.loadTable();
        });
    }
    
    private static void setupModernDarkTheme() {
        try {
            // Use system look and feel as base
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Modern dark color scheme
            Color bgDark = new Color(30, 30, 35);
            Color bgMedium = new Color(40, 40, 45);
            Color bgLight = new Color(50, 50, 55);
            Color fgPrimary = new Color(220, 220, 230);
            Color accent = new Color(100, 150, 255);
            
            // Panel backgrounds
            UIManager.put("Panel.background", bgDark);
            UIManager.put("Panel.foreground", fgPrimary);
            
            // Button styling
            UIManager.put("Button.background", bgLight);
            UIManager.put("Button.foreground", fgPrimary);
            UIManager.put("Button.select", accent);
            UIManager.put("Button.focus", accent);
            UIManager.put("Button.border", BorderFactory.createEmptyBorder(8, 16, 8, 16));
            
            // Text field styling
            UIManager.put("TextField.background", bgMedium);
            UIManager.put("TextField.foreground", fgPrimary);
            UIManager.put("TextField.caretForeground", accent);
            UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgLight, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            
            // ComboBox styling
            UIManager.put("ComboBox.background", bgMedium);
            UIManager.put("ComboBox.foreground", fgPrimary);
            UIManager.put("ComboBox.buttonBackground", bgLight);
            UIManager.put("ComboBox.border", BorderFactory.createLineBorder(bgLight, 1));
            
            // Label styling
            UIManager.put("Label.foreground", fgPrimary);
            
            // Menu styling
            UIManager.put("MenuBar.background", bgMedium);
            UIManager.put("MenuBar.foreground", fgPrimary);
            UIManager.put("Menu.background", bgMedium);
            UIManager.put("Menu.foreground", fgPrimary);
            UIManager.put("MenuItem.background", bgMedium);
            UIManager.put("MenuItem.foreground", fgPrimary);
            UIManager.put("MenuItem.selectionBackground", accent);
            
            // Table styling
            UIManager.put("Table.background", bgDark);
            UIManager.put("Table.foreground", fgPrimary);
            UIManager.put("Table.gridColor", bgLight);
            UIManager.put("Table.selectionBackground", accent);
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("TableHeader.background", bgMedium);
            UIManager.put("TableHeader.foreground", fgPrimary);
            
            // ScrollPane styling
            UIManager.put("ScrollPane.background", bgDark);
            UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
            
            // TextArea styling
            UIManager.put("TextArea.background", bgMedium);
            UIManager.put("TextArea.foreground", fgPrimary);
            UIManager.put("TextArea.caretForeground", accent);
            
            // Slider styling
            UIManager.put("Slider.background", bgDark);
            UIManager.put("Slider.foreground", accent);
            
            // TitledBorder styling
            UIManager.put("TitledBorder.titleColor", fgPrimary);
            
        } catch (Exception e) {
            System.err.println("Failed to set theme: " + e.getMessage());
        }
    }
}
