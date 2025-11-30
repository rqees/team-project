package app;

import view.DataSetTableView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataAnalysisAppBuilder appBuilder = new DataAnalysisAppBuilder();

            JFrame application = appBuilder
                    .addDataSetTableView()
                    .addSearchUseCase()
                    .addTableDisplayUseCase()
                    .loadSampleData()  // Load sample data through builder
                    .build();

            application.setSize(1200, 700);
            application.setLocationRelativeTo(null);
            application.setVisible(true);

            // Display the table
            DataSetTableView tableView = appBuilder.getDataSetTableView();
            tableView.loadTable();
        });
    }
}