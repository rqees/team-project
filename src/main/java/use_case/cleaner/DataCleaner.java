package use_case.cleaner;

import entity.DataSet;
import entity.DataRow;
import entity.Column;
import entity.DataType;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

public class DataCleaner {
    private final DataSet dataSet;

    public DataCleaner(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    // edit a single cell
    public void editCell(int rowIndex, int colIndex, String newValue) {

        // validates
        if (isMissing(newValue)) {
            dataSet.setCell("", rowIndex, colIndex);
            return;
        }

        // Check against that column's datatype
        if (isValidForColumn(colIndex, newValue)) {
            dataSet.setCell(newValue, rowIndex, colIndex);
        } else {
            // Wrong datatype: clean it by making it empty.
            dataSet.setCell("", rowIndex, colIndex);
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
                    dataSet.setCell("", i, j);
                    cleared.add(new CellLocation(i, j));
                }
            }
        }

        return cleared;
    }

    public void  setHeader(String newHeader, int colIndex){
        dataSet.getColumns().get(colIndex).setHeader(newHeader);
    }

    // find missing cells
    public List<CellLocation> findMissingCells() {
        List<CellLocation> missingCells = new ArrayList<>();
        List<DataRow> rows = dataSet.getRows();
        List<String> cells;

        for(int i = 0; i < rows.size(); i++) {
            DataRow row = rows.get(i);
            cells = row.getCells();
            for(int j = 0; j < cells.size(); j++) {
                String value = cells.get(j);
                if(isMissing(value)) {
                    missingCells.add(new CellLocation(i, j));
                }
            }
        }

        return missingCells;
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
}
