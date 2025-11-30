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

        DataSet sampleData = new DataSet(rows, columns);
        gateway.save(sampleData);
    }
}