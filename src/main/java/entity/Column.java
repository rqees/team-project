package entity;

import java.util.List;

public class Column {
    private final List<String> cells;

    public Column(List<String> cells) {
        this.cells = cells;
    }

    public List<String> getCells() {
        return cells;
    }

    public void setCell(String value, int index) {
        this.cells.set(index, value);
    }
}
