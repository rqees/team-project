package use_case.save_dataset;

/**
 * Data returned from the Use Case Interactor to the Presenter.
 */
public class SaveDataSetOutputData {

    private final String datasetId;
    private final boolean success;
    private final String message;

    public SaveDataSetOutputData(String datasetId, boolean success, String message) {
        this.datasetId = datasetId;
        this.success = success;
        this.message = message;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
