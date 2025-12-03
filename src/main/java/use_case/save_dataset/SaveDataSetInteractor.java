package use_case.save_dataset;

import entity.DataSet;
import use_case.dataset.CurrentTableGateway;

/**
 * Interactor that saves the currently loaded dataset through a data access layer.
 */
public final class SaveDataSetInteractor implements SaveDataSetInputBoundary {

    /**
     * Data access layer used to persist datasets.
     */
    private final SaveDataSetDataAccessInterface dataAccess;
    /**
     * Presenter boundary for communicating save results.
     */
    private final SaveDataSetOutputBoundary outputBoundary;
    /**
     * Gateway providing the currently loaded dataset.
     */
    private final CurrentTableGateway currentTableGateway;

    /**
     * Creates a new save dataset interactor.
     *
     * @param dataAccess          persistence interface for saving datasets
     * @param outputBoundary      presenter used to report save results
     * @param currentTableGateway interface for providing the currently loaded dataset
     */
    public SaveDataSetInteractor(final SaveDataSetDataAccessInterface dataAccess,
                                 final SaveDataSetOutputBoundary outputBoundary,
                                 final CurrentTableGateway currentTableGateway) {
        this.dataAccess = dataAccess;
        this.outputBoundary = outputBoundary;
        this.currentTableGateway = currentTableGateway;
    }

    /**
     * Executes the save dataset use case.
     * @param inputData contains the target identifier for the dataset
     */
    @Override
    public void execute(final SaveDataSetInputData inputData) throws java.io.IOException {
        final String id = inputData.getDatasetId();

        if (id == null) {
            outputBoundary.present(new SaveDataSetOutputData(
                    id, false, "Dataset ID is required."
            ));
            return;
        }

        final DataSet dataSet = currentTableGateway.load();
        if (dataSet == null) {
            outputBoundary.present(new SaveDataSetOutputData(
                    id, false, "No dataset loaded to save."
            ));
            return;
        }

        dataAccess.save(id, dataSet);

        outputBoundary.present(new SaveDataSetOutputData(
                id, true, "Dataset saved successfully."
        ));
    }
}
