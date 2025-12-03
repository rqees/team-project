package interface_adapter.save_dataset;

import use_case.save_dataset.SaveDataSetInputBoundary;
import use_case.save_dataset.SaveDataSetInputData;

/**
 * Controller for the save dataset use case, invoked by UI action of clicking the save button.
 */
public final class SaveDataSetController {

    /**
     * Interactor handling the save dataset use case.
     */
    private final SaveDataSetInputBoundary interactor;

    /**
     * Creates a controller for the save dataset use case.
     * @param interactor the interactor that performs the save operation
     */
    public SaveDataSetController(final SaveDataSetInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Initiates a dataset save with the provided identifier or path.
     * @param datasetId name of the saved dataset or path chosen by the user.
     */
    public void execute(final String datasetId) throws java.io.IOException {
        final SaveDataSetInputData inputData = new SaveDataSetInputData(datasetId);
        interactor.execute(inputData);
    }
}
