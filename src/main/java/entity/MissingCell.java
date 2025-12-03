package entity;

public class MissingCell {

    public final int rowIndex;
    public final String columnHeader;

    public MissingCell(int rowIndex, String columnHeader) {
        this.rowIndex = rowIndex;
        this.columnHeader = columnHeader;
    }

    public int getRowIndex(){
        return rowIndex;
    }

    public String getColumnHeader(){
        return columnHeader;
    }

    public String toString() {
        return "(" + rowIndex + ", " + columnHeader + ")";
    }

}
