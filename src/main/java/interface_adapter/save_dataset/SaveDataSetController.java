package interface_adapter.save_dataset;

import use_case.save_dataset.SaveDataSetInputBoundary;
import use_case.save_dataset.SaveDataSetInputData;

public class SaveDataSetController {

    private final SaveDataSetInputBoundary interactor;

    public SaveDataSetController(SaveDataSetInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String datasetId) {
        SaveDataSetInputData inputData = new SaveDataSetInputData(datasetId);
        interactor.execute(inputData);
    }
}
