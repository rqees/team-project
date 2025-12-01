package app;
import view.DataSetTableView;

import javax.swing.*;

public class  Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataAnalysisAppBuilder appBuilder = new DataAnalysisAppBuilder();

            JFrame application = appBuilder
                    .addDataSetTableView()
                    .addSearchUseCase()
                    .addTableDisplayUseCase()
                    .addVisualizationUseCase()
                    .loadSampleData()
                    .build();

            application.setSize(1200, 700);
            application.setLocationRelativeTo(null);
            application.setVisible(true);

            DataSetTableView tableView = appBuilder.getDataSetTableView();
            tableView.loadTable();
        });
    }
}
