package entity;

import java.util.List;

public class DataSubsetSpec {
    private final String subsetId;
    private final List<String> columnNames;
    private final List<Integer> rowIndices;

    public DataSubsetSpec(String subsetId, List<String> columnNames, List<Integer> rowIndices) {
        this.subsetId = subsetId;
        this.columnNames = columnNames;
        this.rowIndices = rowIndices;
    }

    public String getSubsetId() {
        return subsetId;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<Integer> getRowIndices() {
        return rowIndices;
    }
}
