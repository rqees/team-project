package use_case.cleaner;

import entity.DataSet;
import view.DataSetTableView;

import java.util.List;

/**
 * Controller for the Data Cleaning use case
 *
 * - When the user edits a cell, cleaning that cell
 * - Coordinate cleaning of the entire DataSet when requested
 */
public class DataCleaningController {

    private final DataSet dataSet;
    private final DataCleaner dataCleaner;
    private final DataSetTableView view;

    /**
     * Create  a new DataCleaningController
     *
     * @param dataSet the dataset being displayed and cleaned
     * @param dataCleaner the use-case class for cleaning logic
     * @param view the table view that displays the dataset
     */
    public DataCleaningController(DataSet dataSet, DataCleaner dataCleaner, DataSetTableView view) {
        this.dataSet = dataSet;
        this.dataCleaner = dataCleaner;
        this.view = view;
    }

    /**
     * Handle a user editing a single cell in the table.
     *
     * Flow:
     *  1. Ask DataCleaner to clean the raw value for that column.
     *  1.1. if invalid value, DataCleaner will return null, else, will return trimmed value
     *  2. Update the DataSet with the cleaned value.
     *  3. Update the table model in the view with the cleaned value,
     *     using the view's "updatingFromCleaner" flag to avoid
     *     triggering recursive table events.
     *
     * @param rowIndex the row index of the edited cell
     * @param colIndex the column index of the edited cell
     * @param rawValue the new value entered by the user (maybe null or blank)
     */
    public void handleUserEdit(int rowIndex, int colIndex, String rawValue) {
        // 1. Ask DataCleaner to clean the raw value for that column
        String cleanedValue = dataCleaner.cleanValueForColumn(colIndex, rawValue);

        // 2. update the DataSet
        dataSet.setCell(cleanedValue, rowIndex, colIndex);

        // 3. update the table view safely (no infinite listener recursion)
        // will implement these methods/flag in DataSetTableView.
        view.setUpdatingFromCleaner(true);
        view.updateCellFromCleaner(rowIndex, colIndex, cleanedValue);
        view.setUpdatingFromCleaner(false);
    }

    /**
     * Handle a user renaming a column header.
     *
     * Flow:
     *  1. DataCleaner perform the header edit and enforce:
     *      - non-empty header
     *      - unique header
     *  2. update the table header in the view so the
     *     displayed column name matches the underlying DataSet.
     *
     * If the new header is invalid or duplicate, DataCleaner.editHeader will
     * throw an IllegalArgumentException, which can be caught by the caller
     *
     * @param colIndex  the index of the column to rename
     * @param newHeader the new header name the user entered
     */
    public void handleHeaderEdit(int colIndex, String newHeader) {
        // 1. ask DataCleaner to validate and apply the header change to the DataSet
        dataCleaner.editHeader(newHeader, colIndex);

        // 2. update the view so the JTable header reflects the new name
        view.updateColumnHeader(colIndex, newHeader);
    }

    /**
     * Perform cleaning of the entire dataset.
     *
     * called before visualization or summary,
     * and immediately after loading a dataset.
     *
     * Flow:
     *  1. Ask DataCleaner to clean the whole DataSet
     *  1.1 will make invalid cells to Null
     *  2. Notify the view so it can refresh the table from the cleaned DataSet.
     *  3. Return the list of MissingCell as a "log".
     *
     * @return list of missing cells after cleaning (row index + column header)
     */
    public List<MissingCell> cleanDataSet() {
        // 1. delegate bulk cleaning to DataCleaner
        List<MissingCell> missingCells = dataCleaner.cleanDataSet();

        // 2. ask the view to refresh its table contents from the DataSet.
        view.refreshFromDataSet(dataSet);

        // 3. return the log of missing/cleared cells
        return missingCells;
    }
}
