package use_case.cleaner;

import entity.DataSet;
import entity.DataRow;
import entity.Column;
import entity.DataType;
import use_case.dataset.DataSetGateway;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;


/**
 * Use case 3: cleaning and validating a DataSet.
 *
 * - Edit individual cells while enforcing column DataType constraints.
 * - Treat missing or corrupted values as {@code null}.
 * - Provide a log of all cells that were changed to {@code null}.
 */
public class DataCleaner {
    private final DataSet dataSet;

    // Note have to make dataset with this
    // private final DataSetGateway dataSetGateway;

    public DataCleaner(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    // edit a single cell
    public void editCell(int rowIndex, int colIndex, String newValue) {

        // validates
        if (isMissing(newValue)) {
            dataSet.setCell(null, rowIndex, colIndex);
            return;
        }

        // Check against that column's datatype
        if (isValidForColumn(colIndex, newValue)) {
            dataSet.setCell(newValue, rowIndex, colIndex);
        } else {
            // Wrong datatype: clean it by making it empty.
            dataSet.setCell(null, rowIndex, colIndex);
        }

    }

    /**
     * Scan the whole dataset and clear any cells whose value
     * does not match that column's DataType.
     *
     * @return list of locations that were cleared
     */
    public List<CellLocation> cleanInvalidTypeCells() {
        List<CellLocation> cleared = new ArrayList<>();
        List<DataRow> rows = dataSet.getRows();

        for (int i = 0; i < rows.size(); i++) {
            DataRow row = rows.get(i);
            List<String> cells = row.getCells();

            for (int j = 0; j < cells.size(); j++) {
                String value = cells.get(j);

                // Skip already-missing cells
                if (isMissing(value)) {
                    continue;
                }

                if (!isValidForColumn(j, value)) {
                    dataSet.setCell(null, i, j);
                    cleared.add(new CellLocation(i, j));
                }
            }
        }

        return cleared;
    }

    public void  setHeader(String newHeader, int colIndex){
        dataSet.getColumns().get(colIndex).setHeader(newHeader);
    }

    // find missing cells and return it as a log
    public List<MissingCell> findMissingCells() {
        List<MissingCell> missingCells = new ArrayList<>();
        List<DataRow> rows = dataSet.getRows();
        List<Column> columns = dataSet.getColumns();
        List<String> cells;

        for(int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            DataRow row = rows.get(rowIndex);
            cells = row.getCells();

            for(int colIndex = 0; colIndex < cells.size(); colIndex++) {
                String value = cells.get(colIndex);

                if(isMissing(value)) {
                    String header = columns.get(colIndex).getHeader();
                    missingCells.add(new MissingCell(rowIndex, header));
                }
            }
        }

        return missingCells;

        //((row, header),(row, header), ...)
        // Nore: should use column header for cell location
    }

    // fill

    // helper to identify missing cells
    private boolean isMissing(String s) {
        return s == null || s.isBlank();
    }


    // helper to check if the value is valid for that column's datatype
    private boolean isValidForColumn(int colIndex, String value) {
        DataType type = dataSet.getColumns().get(colIndex).getDataType();

        return switch (type) {
            case NUMERIC -> isNumeric(value);
            case BOOLEAN -> isBoolean(value);
            case DATE -> isDate(value);
            case CATEGORICAL -> true;  // any non-empty string is fine
        };
    }

    private boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isBoolean(String value) {
        String v = value.trim().toLowerCase();
        return v.equals("true") || v.equals("false");

    }

    // assumes ISO date format like "2025-03-01"
    private boolean isDate(String value) {
        try {
            LocalDate.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // helper class to identify a cell
    public static class CellLocation {
        public final int row;
        public final int col;

        public CellLocation(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    // helper class that uses columnHeader
    public static class MissingCell {
        public final int rowIndex;
        public final String columnHeader;

        public MissingCell(int rowIndex, String columnHeader) {
            this.rowIndex = rowIndex;
            this.columnHeader = columnHeader;
        }

        public String toString() {
            return "(" + rowIndex + ", " + columnHeader + ")";
        }
    }


}
