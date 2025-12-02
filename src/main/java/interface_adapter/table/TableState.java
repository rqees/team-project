package interface_adapter.table;

public class TableState {
    private String[] columnHeaders = new String[0];
    private String[][] rowData = new String[0][0];
    private String errorMessage;

    public String[] getColumnHeaders() {
        return columnHeaders;
    }

    public void setColumnHeaders(String[] columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    public String[][] getRowData() {
        return rowData;
    }

    public void setRowData(String[][] rowData) {
        this.rowData = rowData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}