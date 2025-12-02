package interface_adapter.cleaner;

import use_case.cleaner.DataCleaningInputBoundary;
import use_case.cleaner.DataCleaningInputData;

/**
 * Controller for the Data Cleaning use case.
 **/
public class DataCleaningController {

    private final DataCleaningInputBoundary interactor;

    public DataCleaningController(DataCleaningInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Handle a user editing a single cell in the table.
     *
     * @param rowIndex the row index of the edited cell
     * @param colIndex the column index of the edited cell
     * @param rawValue the new value entered by the user
     */
    public void handleUserEdit(int rowIndex, int colIndex, String rawValue) {
        DataCleaningInputData.EditedCellInputData inputData =
                new DataCleaningInputData.EditedCellInputData(rowIndex, colIndex, rawValue);

        interactor.cleanEditedCell(inputData);
    }

    /**
     * Handle a user renaming a column header.
     *
     * @param colIndex the index of the column to rename
     * @param newHeader the new header name the user entered
     */
    public void handleHeaderEdit(int colIndex, String newHeader) {
        DataCleaningInputData.HeaderEditInputData inputData =
                new DataCleaningInputData.HeaderEditInputData(colIndex, newHeader);

        interactor.editHeader(inputData);
    }

    public void handleCleanEntireDataSet() {
        interactor.cleanEntireDataSet();
    }


}
