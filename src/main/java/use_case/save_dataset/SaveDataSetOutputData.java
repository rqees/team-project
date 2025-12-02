package use_case.save_dataset;

/**
 * Data returned from the Save Dataset interactor to the presenter.
 */
public class SaveDataSetOutputData {

    private final String datasetId;
    private final boolean success;
    private final String message;

    /**
     * Constructs an output model for the save dataset use case.
     *
     * @param datasetId the identifier that was requested to save the dataset under.
     * @param success   whether the save operation was successful
     * @param message    status message describing the save result.
     */
    public SaveDataSetOutputData(String datasetId, boolean success, String message) {
        this.datasetId = datasetId;
        this.success = success;
        this.message = message;
    }

    /**
     * Returns the dataset identifier that was attempted to be saved.
     *
     * @return dataset identifier
     */
    public String getDatasetId() {
        return datasetId;
    }

    /**
     * Indicates if the save operation completed successfully.
     *
     * @return true when save succeeded, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the human-readable status message describing the save result.
     *
     * @return status message
     */
    public String getMessage() {
        return message;
    }
}
