package interface_adapter.save_dataset;

import entity.DataSet;
import use_case.save_dataset.SaveDataSetInputBoundary;
import use_case.save_dataset.SaveDataSetInputData;

public class SaveDataSetController {

    private final SaveDataSetInputBoundary interactor;

    public SaveDataSetController(SaveDataSetInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void save(String datasetId, DataSet dataSet) {
        SaveDataSetInputData inputData = new SaveDataSetInputData(datasetId, dataSet);
        interactor.save(inputData);
    }
}
