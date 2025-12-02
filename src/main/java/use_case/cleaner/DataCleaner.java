package use_case.cleaner;

import entity.DataSet;
import entity.DataRow;

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


        dataSet.setCell(newValue, rowIndex, colIndex);
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
