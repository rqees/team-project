package app;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import entity.DataType;
import view.DataSetTableView;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataAnalysisAppBuilder appBuilder = new DataAnalysisAppBuilder();

            JFrame application = appBuilder
                    .addDataSetTableView()
                    .addSearchUseCase()
                    .build();

            application.setSize(1200, 700);
            application.setLocationRelativeTo(null);
            application.setVisible(true);

            // Load sample data for demonstration
            DataSetTableView tableView = appBuilder.getDataSetTableView();
            DataSet sampleData = createSampleData();
            tableView.displayDataSet(sampleData);
        });
    }

    private static DataSet createSampleData() {
        List<DataRow> rows = Arrays.asList(
                new DataRow(Arrays.asList("John", "25", "Engineer", "New York")),
                new DataRow(Arrays.asList("Jane", "30", "Doctor", "Boston")),
                new DataRow(Arrays.asList("Bob", "28", "Teacher", "Chicago")),
                new DataRow(Arrays.asList("Alice", "32", "Designer", "Seattle")),
                new DataRow(Arrays.asList("Charlie", "27", "Developer", "Austin"))
        );

        List<Column> columns = Arrays.asList(
                new Column(Arrays.asList("John", "Jane", "Bob", "Alice", "Charlie"),
                        DataType.CATEGORICAL, "Name"),
                new Column(Arrays.asList("25", "30", "28", "32", "27"),
                        DataType.NUMERIC, "Age"),
                new Column(Arrays.asList("Engineer", "Doctor", "Teacher", "Designer", "Developer"),
                        DataType.CATEGORICAL, "Occupation"),
                new Column(Arrays.asList("New York", "Boston", "Chicago", "Seattle", "Austin"),
                        DataType.CATEGORICAL, "Location")
        );

        return new DataSet(rows, columns);
    }
}