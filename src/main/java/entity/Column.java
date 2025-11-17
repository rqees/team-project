package entity;

import java.util.List;

public class Column {
    private final List<String> cells;
    private final Object datatype;

    public Column(List<String> cells, Object datatype) {
        this.cells = cells;
        this.datatype = datatype;
    }

    public List<String> getCells() {
        return cells;
    }

    public Object getDataType() {
        return datatype;
    }

    protected void setCell(String value, int index) {
        this.cells.set(index, value);
    }
}
