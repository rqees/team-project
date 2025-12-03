package use_case.cleaner;
/**
 * Container for all input data types of the Data Cleaning use case.
 **/
public class DataCleaningInputData {
    /**
     * Input data for editing/cleaning a single cell.
     */
    public static class EditedCellInputData {
        private final int rowIndex;
        private final int colIndex;
        private final String rawValue;

        public EditedCellInputData(int rowIndex, int colIndex, String rawValue) {
            this.rowIndex = rowIndex;
            this.colIndex = colIndex;
            this.rawValue = rawValue;
        }

        public int getRowIndex() {
            return rowIndex;
        }

        public int getColIndex() {
            return colIndex;
        }

        public String getRawValue() {
            return rawValue;
        }
    }

    /**
     * Input data for editing a column header.
     */
    public static class HeaderEditInputData {
        private final int colIndex;
        private final String newHeader;

        public HeaderEditInputData(int colIndex, String newHeader) {
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

}
