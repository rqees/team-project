package use_case.cleaner;

import java.util.List;

public interface  DataCleaningInputBoundary {
    /** User edited a single cell. */
    void cleanEditedCell(DataCleaningInputData.EditedCellInputData inputData);

    /** User edited a column header. */
    void editHeader(DataCleaningInputData.HeaderEditInputData inputData);

    /** Clean the entire dataset once **/
    void cleanEntireDataSet();

}
