package use_case.save_dataset;

/**
 * Input data sent from the Controller to the Use Case Interactor.
 */
public class SaveDataSetInputData {

    private final String datasetId;

    public SaveDataSetInputData(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getDatasetId() {
        return datasetId;
    }
}
