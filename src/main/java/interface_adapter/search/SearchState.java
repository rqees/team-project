package interface_adapter.search;

/**
 * The State for the Search View Model.
 */
public class SearchState {
    private int row = -1;
    private int column = -1;
    private boolean found = false;
    private String errorMessage = null;

    public SearchState(SearchState copy) {
        this.row = copy.row;
        this.column = copy.column;
        this.found = copy.found;
        this.errorMessage = copy.errorMessage;
    }

    public SearchState() {
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
