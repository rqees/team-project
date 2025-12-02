package app;

import view.DataSetTableView;

import javax.swing.*;
import java.awt.*;

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
                    .loadSampleData()  // Load sample data through builder
                    .build();

            application.setSize(1400, 900);
            application.setLocationRelativeTo(null);
            application.setVisible(true);

            // Display the table
            DataSetTableView tableView = appBuilder.getDataSetTableView();
            tableView.loadTable();
        });
    }
}
