package use_case.cleaner;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import entity.DataType;
import entity.MissingCell;
import use_case.cleaner.validators.BooleanValidator;
import use_case.cleaner.validators.CategoricalValidator;
import use_case.cleaner.validators.DataTypeValidator;
import use_case.cleaner.validators.DateValidator;
import use_case.cleaner.validators.NumericValidator;
import use_case.dataset.CurrentTableGateway;

/**
 * Use case 3: cleaning and validating a DataSet.
 * - Edit individual cells while enforcing column DataType constraints.
 * - Treat missing or corrupted values as {@code null}.
 * - Provide a log of all cells that were changed to {@code null}.
 */
public class DataCleanerInteractor implements DataCleaningInputBoundary {

    private final CurrentTableGateway tableGateway;
    private final DataCleaningOutputBoundary presenter;

    private final Set<String> uniqueHeaders = new HashSet<>();
    private final Map<DataType, DataTypeValidator> validators =
            new EnumMap<>(DataType.class);

    public DataCleanerInteractor(CurrentTableGateway tableGateway, DataCleaningOutputBoundary presenter) {
        this.tableGateway = tableGateway;
        this.presenter = presenter;
        initializeValidators();
    }

    // helper to always get the current DataSet
    private DataSet requireCurrentDataSet() {
        final DataSet dataSet = tableGateway.load();
        if (dataSet == null) {
            throw new IllegalStateException("No current dataset is loaded.");
        }
        return dataSet;
    }

    // Input boundary methods---------------
    @Override
    public void cleanEditedCell(DataCleaningInputData.EditedCellInputData inputData) {
        final DataSet dataSet = requireCurrentDataSet();

        final int rowIndex = inputData.getRowIndex();
        final int colIndex = inputData.getColIndex();
        final String rawValue = inputData.getRawValue();

        // 1. clean the value using existing logic
        final String cleanedValue = cleanValueForColumn(dataSet, colIndex, rawValue);

        // 2. update the entity (DataSet)
        dataSet.setCell(cleanedValue, rowIndex, colIndex);
        tableGateway.save(dataSet);

        // 3. build output data and send to presenter
        final DataCleaningOutputData.EditedCellOutputData outputData =
                new DataCleaningOutputData.EditedCellOutputData(
                        rowIndex, colIndex, cleanedValue
                );

        presenter.presentEditedCell(outputData);
    }

    @Override
    public void editHeader(DataCleaningInputData.HeaderEditInputData inputData) {
        final DataSet dataSet = requireCurrentDataSet();

        final int colIndex = inputData.getColIndex();
        final String newHeader = inputData.getNewHeader();

        try {
            // use helper that contains header logic
            editHeaderInternal(dataSet, newHeader, colIndex);
            tableGateway.save(dataSet);

            final DataCleaningOutputData.HeaderEditOutputData outputData =
                    new DataCleaningOutputData.HeaderEditOutputData(colIndex, newHeader);

            presenter.presentHeaderEdit(outputData);
        }
        catch (IllegalArgumentException err) {
            // report by output boundary
            presenter.presentHeaderEditFailure(err.getMessage());
        }
    }

    @Override
    public void cleanEntireDataSet() {
        final DataSet dataSet = requireCurrentDataSet();

        // use old cleanDataSet logic
        final List<MissingCell> changedToNull = cleanDataSetInternal(dataSet);
        tableGateway.save(dataSet);

        final DataCleaningOutputData.CleanEntireDataSetOutputData outputData =
                new DataCleaningOutputData.CleanEntireDataSetOutputData(changedToNull);

        // send to presenter
        presenter.presentEntireDataSetCleaned(outputData);
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
     * @param dataSet the current dataSet being edited
     * @return list of locations that were cleared using header
     */
    private List<MissingCell> cleanDataSetInternal(DataSet dataSet) {
        final List<MissingCell> changedToNull = new ArrayList<>();

        final List<DataRow> rows = dataSet.getRows();
        final List<Column> columns = dataSet.getColumns();

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            final DataRow row = rows.get(rowIndex);
            final List<String> cells = row.getCells();

            for (int colIndex = 0; colIndex < cells.size(); colIndex++) {
                final String originalValue = cells.get(colIndex);

                final String cleanValue = cleanValueForColumn(dataSet, colIndex, originalValue);

                dataSet.setCell(cleanValue, rowIndex, colIndex);

                // check if changed to null
                if (!isMissing(originalValue) && cleanValue == null) {
                    // add to the list
                    final String header = columns.get(colIndex).getHeader();
                    changedToNull.add(new MissingCell(rowIndex, header));
                }
            }
        }

        return changedToNull;
    }

    // Edit a column header
    // check if new header is null or duplicate
    // if true, throw exception
    private void editHeaderInternal(DataSet dataSet, String newHeader, int colIndex) {
        rebuildUniqueHeaders(dataSet);

        // check if null
        if (isMissing(newHeader)) {
            throw new IllegalArgumentException("Header cannot be empty");
        }

        // case-insensitive
        final String cleanNewHeader = newHeader.trim().toLowerCase();
        final String oldHeader = dataSet.getColumns().get(colIndex).getHeader();
        final String cleanOld = oldHeader.trim().toLowerCase();

        // check duplicate
        if (uniqueHeaders.contains(cleanNewHeader)) {
            throw new IllegalArgumentException("Header already exists");
        }

        // update uniqueHeader
        uniqueHeaders.remove(cleanOld);
        uniqueHeaders.add(cleanNewHeader);

        dataSet.getColumns().get(colIndex).setHeader(newHeader);

    }

    // helper to rebuild UniqueHeaders each time
    private void rebuildUniqueHeaders(DataSet dataSet) {
        uniqueHeaders.clear();
        for (Column column : dataSet.getColumns()) {
            final String header = column.getHeader();
            if (header == null) {
                throw new IllegalArgumentException("Column header cannot be null.");
            }
            final String cleanHeader = header.trim().toLowerCase();
            if (uniqueHeaders.contains(cleanHeader)) {
                throw new IllegalArgumentException("Column header already exists.");
            }
            uniqueHeaders.add(cleanHeader);
        }
    }

    /**
     * Find missing cells and return it as a log.
     * @return list (rowIndex, columnHeader) of all null cells
     **/
    public List<MissingCell> findMissingCells() {
        final DataSet dataSet = requireCurrentDataSet();

        final List<MissingCell> missingCells = new ArrayList<>();

        final List<DataRow> rows = dataSet.getRows();
        final List<Column> columns = dataSet.getColumns();

        List<String> cells;

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            final DataRow row = rows.get(rowIndex);
            cells = row.getCells();

            for (int colIndex = 0; colIndex < cells.size(); colIndex++) {
                final String value = cells.get(colIndex);

                if (isMissing(value)) {
                    final String header = columns.get(colIndex).getHeader();
                    missingCells.add(new MissingCell(rowIndex, header));
                }
            }
        }

        return missingCells;
    }

    // helper to identify missing cells
    private boolean isMissing(String str) {
        return str == null || str.isBlank();
    }

    /**
     * Clean the given raw value for the column at the given index.
     * - If the value is null or blank -> returns null.
     * - If the value is non-blank but fails validation -> returns null.
     * - If it passes validation -> returns the trimmed value.
     * @param dataSet the current dataSet on program
     * @param columnIndex int index for the column that is being checked
     * @param newValue ne value user give to cell
     * @return cleaned String if valid, Null if invalid,
     */
    public String cleanValueForColumn(DataSet dataSet, int columnIndex, String newValue) {
        String result = null;

        if (newValue != null) {

            final String trimmed = newValue.trim();

            if (!trimmed.isEmpty()) {
                // get correct validator for the column
                final DataTypeValidator validator = getValidatorForColumn(dataSet, columnIndex);
                final boolean valid = validator.isValid(trimmed);

                if (valid) {
                    // invalid values are saved as null
                    result = trimmed;
                }
            }
        }
        return result;
    }

    // helper to get right validator for allowed data type
    private DataTypeValidator getValidatorForColumn(DataSet dataSet, int columnIndex) {
        final DataType type = dataSet.getColumns().get(columnIndex).getDataType();
        final DataTypeValidator validator = validators.get(type);

        return validator;
    }

}
