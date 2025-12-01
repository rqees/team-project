package entity;

import java.util.List;

public class DataSubsetSpec {
    private final String datasetId;
    private final List<String> columnNames;
    private final List<Integer> rowIndices;

    public DataSubsetSpec(String datasetId, List<String> columnNames, List<Integer> rowIndices) {
        this.datasetId = datasetId;
        this.columnNames = columnNames;
        this.rowIndices = rowIndices;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<Integer> getRowIndices() {
        return rowIndices;
    }
}
