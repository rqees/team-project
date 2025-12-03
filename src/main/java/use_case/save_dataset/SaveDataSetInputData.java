package use_case.save_dataset;

/**
 * Input data sent from the controller to the save dataset interactor.
 */
public class SaveDataSetInputData {

    /**
     * Identifier to save the dataset under.
     */
    private final String datasetId;

    /**
     * Creates input data for saving a dataset.
     *
     * @param datasetId identifier to associate with the saved dataset
     */
    public SaveDataSetInputData(final String datasetId) {
        this.datasetId = datasetId;
    }

    /**
     * Returns the identifier to save the dataset under.
     *
     * @return dataset identifier
     */
    public String getDatasetId() {
        return datasetId;
    }
}
