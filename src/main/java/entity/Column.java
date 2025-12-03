package entity;

import java.util.List;

public class Column {
    private final List<String> cells;
    private final DataType datatype;
    private String header;

    public Column(List<String> cells, DataType datatype, String header) {
        this.cells = cells;
        this.datatype = datatype;
        this.header = header;
    }

    public List<String> getCells() {

        return cells;
    }

    public DataType getDataType() {
        return datatype;
    }

    public String getHeader() {
        return header;
    }

    protected void setCell(String value, int index) {
        this.cells.set(index, value);
    }

    public void setHeader(String newHeader) {
        this.header = newHeader;
    }
}
