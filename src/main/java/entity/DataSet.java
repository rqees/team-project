package entity;

import java.util.List;

public class DataSet {
    private final List<DataRow> rows;
    private final List<Column> columns;

    public DataSet(List<DataRow> rows,  List<Column> columns) {
        this.rows = rows;
        this.columns = columns;
    }
}
