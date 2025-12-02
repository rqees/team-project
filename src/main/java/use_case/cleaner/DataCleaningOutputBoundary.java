package use_case.cleaner;

/**
 * Output boundary for the Data Cleaning use case.
 */

public interface DataCleaningOutputBoundary {
    /** Present the result of cleaning a single edited cell. */
    void presentEditedCell(DataCleaningOutputData.EditedCellOutputData outputData);

    /** Present the result of successfully editing a column header. */
    void presentHeaderEdit(DataCleaningOutputData.HeaderEditOutputData outputData);

    /** Present an error that occurred while editing a header. */
    void presentHeaderEditFailure(String errorMessage);

    /** Present the result of cleaning the entire dataset. */
    void presentEntireDataSetCleaned(
            DataCleaningOutputData.CleanEntireDataSetOutputData outputData);
}

