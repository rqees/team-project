package use_case.cleaner;

import entity.DataSet;
import entity.DataRow;
import entity.Column;
import entity.DataType;

import use_case.cleaner.validators.BooleanValidator;
import use_case.cleaner.validators.CategoricalValidator;
import use_case.cleaner.validators.DataTypeValidator;
import use_case.cleaner.validators.DateValidator;
import use_case.cleaner.validators.NumericValidator;

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

    /**
     * Scan the whole dataset and set to null for any cells whose value
     * does not match that column's DataType.
     *
     * @return list of locations that were cleared using header
     */
    public List<MissingCell> cleanDataSet() {
        List<MissingCell> changedToNull = new ArrayList<>();

        List<DataRow> rows = dataSet.getRows();
        List<Column> columns = dataSet.getColumns();

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            DataRow row = rows.get(rowIndex);
            List<String> cells = row.getCells();

            for (int colIndex = 0; colIndex < cells.size(); colIndex++) {
                String originalValue = cells.get(colIndex);

                String cleanValue = cleanValueForColumn(colIndex, originalValue);

                dataSet.setCell(cleanValue, rowIndex, colIndex);

                // check if changed to null
                if (!isMissing(originalValue) && cleanValue == null) {
                    // add to the list
                    String header = columns.get(colIndex).getHeader();
                    changedToNull.add(new MissingCell(rowIndex, header));
                }


            }
        }

        return changedToNull;
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
        String oldHeader = dataSet.getColumns().get(colIndex).getHeader();
        String cleanOld = oldHeader.trim().toLowerCase();

        // no change
        if (cleanNewHeader.equals(cleanOld)) {
            return;
        }
        // check duplicate
        else if (uniqueHeaders.contains(cleanNewHeader)) {
            throw new IllegalArgumentException("Header already exists");
        }

        // update uniqueHeader

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

}
