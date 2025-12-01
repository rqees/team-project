package use_case.save_dataset;

import entity.DataSet;

/**
 * Input data sent from the Controller to the Use Case Interactor.
 */
public class SaveDataSetInputData {

    private final String datasetId;
    private final DataSet dataSet;

    public SaveDataSetInputData(String datasetId, DataSet dataSet) {
        this.datasetId = datasetId;
        this.dataSet = dataSet;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public DataSet getDataSet() {
        return dataSet;
    }
}
