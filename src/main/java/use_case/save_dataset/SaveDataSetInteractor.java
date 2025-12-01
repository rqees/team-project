package use_case.save_dataset;

import entity.DataSet;

/**
 * [fill]
 */
public class SaveDataSetInteractor implements SaveDataSetInputBoundary {

    private final SaveDataSetDataAccessInterface dataAccess;
    private final SaveDataSetOutputBoundary outputBoundary;
    private final CurrentTableGateway currentTableGateway;

    public SaveDataSetInteractor(SaveDataSetDataAccessInterface dataAccess,
                                 SaveDataSetOutputBoundary outputBoundary,
                                 CurrentTableGateway currentTableGateway) {
        this.dataAccess = dataAccess;
        this.outputBoundary = outputBoundary;
        this.currentTableGateway = currentTableGateway;
    }

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
