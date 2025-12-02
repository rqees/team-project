package use_case.table;

public class DisplayTableOutputData {
    private final String[] headers;
    private final String[][] rowData;

    public DisplayTableOutputData(String[] headers, String[][] rowData) {
        this.headers = headers;
        this.rowData = rowData;
    }

    public String[] getHeaders() {
        return headers;
    }
    public String[][] getRowData() {
        return rowData;
    }
}
