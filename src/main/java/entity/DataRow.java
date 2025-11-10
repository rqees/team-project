package entity;

import java.util.List;

public class DataRow {
    private final List<String> cells;

    public DataRow(List<String> cells) {
        this.cells = cells;
    }

    public List<String> getCells() {
        return cells;
    }

    public void setCell(String value, int index) {
        this.cells.set(index, value);
    }
}
