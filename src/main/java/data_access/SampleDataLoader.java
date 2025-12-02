package data_access;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import entity.DataType;
import use_case.dataset.CurrentTableGateway;

import java.util.Arrays;
import java.util.List;

/**
 * Sample Data
 */
public class SampleDataLoader {

public static void loadSampleData(CurrentTableGateway gateway) {
    List<DataRow> rows = Arrays.asList(
            new DataRow(Arrays.asList("John", "25", "Engineer", "New York", "85000")),
            new DataRow(Arrays.asList("Jane", "30", "Doctor", "Boston", "250000")),
            new DataRow(Arrays.asList("Bob", "28", "Teacher", "Chicago", "45000")),
            new DataRow(Arrays.asList("Alice", "32", "Designer", "Seattle", "120000")),
            new DataRow(Arrays.asList("Charlie", "27", "Developer", "Austin", "95000"))
    );
    List<Column> columns = Arrays.asList(
            new Column(Arrays.asList("John", "Jane", "Bob", "Alice", "Charlie"),
                    DataType.CATEGORICAL, "Name"),
            new Column(Arrays.asList("25", "30", "28", "32", "27"),
                    DataType.NUMERIC, "Age"),
            new Column(Arrays.asList("Engineer", "Doctor", "Teacher", "Designer", "Developer"),
                    DataType.CATEGORICAL, "Occupation"),
            new Column(Arrays.asList("New York", "Boston", "Chicago", "Seattle", "Austin"),
                    DataType.CATEGORICAL, "Location"),
            new Column(Arrays.asList("85000", "250000", "45000", "120000", "95000"),
                    DataType.NUMERIC, "Net Worth")
    );

        DataSet sampleData = new DataSet(rows, columns);
        gateway.save(sampleData);
    }
}