package use_case.search;

/**
 * Output Data for the Search Use Case.
 */
public class SearchOutputData {
    private final int row;
    private final int column;
    private final boolean found;

    public SearchOutputData(int row, int column, boolean found) {
        this.row = row;
        this.column = column;
        this.found = found;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isFound() {
        return found;
    }
}