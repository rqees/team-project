package use_case.cleaner;

import entity.MissingCell;
import java.util.List;

public class DataCleaningOutputData {

    /**
     * Output data for cleaning a single edited cell.
     */
    public static class EditedCellOutputData {
        private final int rowIndex;
        private final int colIndex;
        private final String cleanedValue;  // may be null if invalid -> missing

        public EditedCellOutputData(int rowIndex, int colIndex, String cleanedValue) {
            this.rowIndex = rowIndex;
            this.colIndex = colIndex;
            this.cleanedValue = cleanedValue;
        }

        public int getRowIndex() {
            return rowIndex;
        }

        public int getColIndex() {
            return colIndex;
        }

        public String getCleanedValue() {
            return cleanedValue;
        }
    }

    /**
     * Output data for a successful column header edit.
     */
    public static class HeaderEditOutputData {
        private final int colIndex;
        private final String newHeader;

        public HeaderEditOutputData(int colIndex, String newHeader) {
            this.colIndex = colIndex;
            this.newHeader = newHeader;
        }

        public int getColIndex() {
            return colIndex;
        }

        public String getNewHeader() {
            return newHeader;
        }
    }

    /**
     * Output data for cleaning the entire dataset.
     * Contains a log of cells that became missing (null) after cleaning.
     */
    public static class CleanEntireDataSetOutputData {
        private final List<MissingCell> missingCells;

        public CleanEntireDataSetOutputData(List<MissingCell> missingCells) {
            this.missingCells = missingCells;
        }

        public List<MissingCell> getMissingCells() {
            return missingCells;
        }
    }
}
