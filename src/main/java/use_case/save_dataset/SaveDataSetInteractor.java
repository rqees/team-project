package use_case.save_dataset;

import entity.DataSet;
import use_case.dataset.CurrentTableGateway;

/**
 * Interactor that saves the currently loaded dataset through a data access layer.
 */
public class SaveDataSetInteractor implements SaveDataSetInputBoundary {

    private final SaveDataSetDataAccessInterface dataAccess;
    private final SaveDataSetOutputBoundary outputBoundary;
    private final CurrentTableGateway currentTableGateway;

    /**
     * Creates a new save dataset interactor.
     *
     * @param dataAccess          persistence interface for saving datasets
     * @param outputBoundary      presenter used to report save results
     * @param currentTableGateway interface for providing the currently loaded dataset
     */
    public SaveDataSetInteractor(SaveDataSetDataAccessInterface dataAccess,
                                 SaveDataSetOutputBoundary outputBoundary,
                                 CurrentTableGateway currentTableGateway) {
        this.dataAccess = dataAccess;
        this.outputBoundary = outputBoundary;
        this.currentTableGateway = currentTableGateway;
    }

    /**
     * Executes the save dataset use case.
     *
     * @param inputData contains the target identifier for the dataset
     */
    @Override
    public void execute(SaveDataSetInputData inputData) {
        String id = inputData.getDatasetId();

        if (id == null || id.isBlank()) {
            outputBoundary.present(new SaveDataSetOutputData(
                    id, false, "Dataset ID cannot be empty."
            ));
            return;
        }

        DataSet dataSet = currentTableGateway.load();
        if (dataSet == null) {
            outputBoundary.present(new SaveDataSetOutputData(
                    id, false, "No dataset loaded to save."
            ));
            return;
        }

        try {
            dataAccess.save(id, dataSet);

            outputBoundary.present(new SaveDataSetOutputData(
                    id, true, "Dataset saved successfully."
            ));

        } catch (RuntimeException e) {
            outputBoundary.present(new SaveDataSetOutputData(
                    id, false, "Failed to save dataset: " + e.getMessage()
            ));
        }
    }
}
