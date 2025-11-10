package entity;

import java.util.List;

public class DataSet {
    private final List<DataRow> rows;
    private final List<Column> columns;

    public DataSet(List<DataRow> rows,  List<Column> columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public List<DataRow> getRows() {
        return rows;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setCell(String value, int row, int column) {
        this.rows.get(row).setCell(value, column);
        this.columns.get(column).setCell(value, row);
    }
}
