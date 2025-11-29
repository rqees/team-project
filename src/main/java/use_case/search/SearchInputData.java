package use_case.search;

/**
 * Input Data for the Search Use Case.
 */
public class SearchInputData {
    private final String searchTerm;
    private final String[][] tableData;
    private final int startRow;
    private final int startColumn;

    public SearchInputData(String searchTerm, String[][] tableData, int startRow, int startColumn) {
        this.searchTerm = searchTerm;
        this.tableData = tableData;
        this.startRow = startRow;
        this.startColumn = startColumn;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public String[][] getTableData() {
        return tableData;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartColumn() {
        return startColumn;
    }
}

