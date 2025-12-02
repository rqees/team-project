package app;

import view.DataSetTableView;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Set modern dark theme
            setupModernDarkTheme();
            
            DataAnalysisAppBuilder appBuilder = new DataAnalysisAppBuilder();

            JFrame application = appBuilder
                    .addDataSetTableView()
                    .addSearchUseCase()
                    .addTableDisplayUseCase()
                    .addLoadUseCase()
                    .addStatisticsUseCase()
                    .addSaveUseCase()
                    .addDataCleaningUseCase()
                    .loadSampleData()  // Load sample data through builder
                    .addVisualizationUseCase()
                    .build();

            application.setSize(1400, 900);
            application.setLocationRelativeTo(null);
            application.setVisible(true);

            // Display the table
            DataSetTableView tableView = appBuilder.getDataSetTableView();
            tableView.loadTable();
        });
    }
    private static void setupModernDarkTheme() {
        try {
            // Use system look and feel as base
            UIManager.setLookAndFeel(new FlatDarculaLaf());
    
            
        } catch (Exception e) {
            System.err.println("Failed to set theme: " + e.getMessage());
        }
    }
}
