package interface_adapter.save_dataset;

import use_case.save_dataset.SaveDataSetInputBoundary;
import use_case.save_dataset.SaveDataSetInputData;

/**
 * Controller for the save dataset use case, invoked by UI action of clicking the save button.
 */
public class SaveDataSetController {

    private final SaveDataSetInputBoundary interactor;

    /**
     * Creates a controller for the save dataset use case.
     *
     * @param interactor the interactor that performs the save operation
     */
    public SaveDataSetController(SaveDataSetInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Initiates a dataset save with the provided identifier or path.
     *
     * @param datasetId name of the saved dataset or path chosen by the user.
     */
    public void execute(String datasetId) {
        SaveDataSetInputData inputData = new SaveDataSetInputData(datasetId);
        interactor.execute(inputData);
    }
}
