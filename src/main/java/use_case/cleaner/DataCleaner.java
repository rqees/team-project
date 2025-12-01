package use_case.cleaner;

import entity.DataSet;
import entity.DataRow;
import entity.Column;
import entity.DataType;

import entity.DataType;
import use_case.cleaner.validators.BooleanValidator;
import use_case.cleaner.validators.CategoricalValidator;
import use_case.cleaner.validators.DataTypeValidator;
import use_case.cleaner.validators.DateValidator;
import use_case.cleaner.validators.NumericValidator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.EnumMap;
import java.util.Map;

/**
 * Use case 3: cleaning and validating a DataSet.
 *
 * - Edit individual cells while enforcing column DataType constraints.
 * - Treat missing or corrupted values as {@code null}.
 * - Provide a log of all cells that were changed to {@code null}.
 */
public class DataCleaner {
    private final DataSet dataSet;
    private final Set<String> uniqueHeaders = new HashSet<>();
    private final Map<DataType, DataTypeValidator> validators =
            new EnumMap<>(DataType.class);

    // Note have to make dataset with this
    // private final DataSetGateway dataSetGateway;

    public DataCleaner(DataSet dataSet) {
        this.dataSet = dataSet;
        checkUniqueHeaders();
        initializeValidators();
    }

    // initialize validators to validate the data
    private void initializeValidators() {
        validators.put(DataType.NUMERIC, new NumericValidator());
        validators.put(DataType.CATEGORICAL, new CategoricalValidator());
        validators.put(DataType.DATE, new DateValidator());
        validators.put(DataType.BOOLEAN, new BooleanValidator());
    }

    // edit a single cell
    public void editCell(int rowIndex, int colIndex, String newValue) {

        // validates
        if (isMissing(newValue)) {
            dataSet.setCell(null, rowIndex, colIndex);
            return;
        }

        // cleanValueForColumn returns null if it is invalid datatype
        // a value if it is valid
        dataSet.setCell(cleanValueForColumn(colIndex, newValue), rowIndex, colIndex);
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


    // Edit a column header
    // check if new header is null or duplicate
    // if true, throw exception
    public void editHeader(String newHeader, int colIndex){

        // check if null
        if (isMissing(newHeader)) {
            throw new IllegalArgumentException("Header cannot be empty");
        }

        // case-insensitive
        String cleanNewHeader = newHeader.trim().toLowerCase();
        // check duplicate
        if (uniqueHeaders.contains(cleanNewHeader)) {
            throw new IllegalArgumentException("Header already exists");
        }

        // update uniqueHeader
        String oldHeader = dataSet.getColumns().get(colIndex).getHeader();
        String cleanOld = oldHeader.trim().toLowerCase();
        uniqueHeaders.remove(cleanOld);
        uniqueHeaders.add(cleanNewHeader);

        dataSet.getColumns().get(colIndex).setHeader(newHeader);
    }


    // check for duplicate header for the columns
    // if the header is null or duplicate, throw an exception
    // header is case-insensitive
    private void checkUniqueHeaders() {

        for(Column column : dataSet.getColumns()){
            String header = column.getHeader();

            if(header == null){
                // not sure what to do when header is empty.
                // It just throws Exception right now
                throw new IllegalArgumentException("Column header cannot be null.");
            }

            String cleanHeader = header.trim().toLowerCase();

            if(uniqueHeaders.contains(cleanHeader)){
                throw new IllegalArgumentException("Column header already exists.");
            }

            uniqueHeaders.add(cleanHeader);
        }
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


    /**
     * Clean the given raw value for the column at the given index.
     * - If the value is null or blank -> returns null.
     * - If the value is non-blank but fails validation -> returns null.
     * - If it passes validation -> returns the trimmed value.
     */
    public String cleanValueForColumn(int columnIndex, String newValue) {
        if (newValue == null) {
            return null;
        }

        String trimmed = newValue.trim();
        if (trimmed.isEmpty()) {
            // treat blank as missing
            return null;
        }

        // get correct validator for the column
        DataTypeValidator validator = getValidatorForColumn(columnIndex);
        boolean valid = validator.isValid(trimmed);

        if (!valid) {
            // invalid values are saved as null
            return null;
        }

        // valid value: keep the trimmed string
        return trimmed;
    }

    // helper to get right validator for allowed data type
    private DataTypeValidator getValidatorForColumn(int columnIndex) {
        DataType type = dataSet.getColumns().get(columnIndex).getDataType();
        DataTypeValidator validator = validators.get(type);

        if (validator == null) {
            throw new IllegalStateException(
                    "No validator configured for data type: " + type);
        }

        return validator;
    }

    // helper that returns if cleanValueForColumn returns null or not
    // null - false
    // value - true
    private boolean isValidForColumn(int colIndex, String newValue) {
        String cleaned = cleanValueForColumn(colIndex, newValue);
        return cleaned != null;
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
