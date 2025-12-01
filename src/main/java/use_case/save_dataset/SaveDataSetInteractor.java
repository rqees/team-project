package use_case.save_dataset;

import entity.DataSet;

/**
 * [fill]
 */
public class SaveDataSetInteractor implements SaveDataSetInputBoundary {

    private final SaveDataSetDataAccessInterface dataAccess;
    private final SaveDataSetOutputBoundary outputBoundary;

    public SaveDataSetInteractor(SaveDataSetDataAccessInterface dataAccess,
                                 SaveDataSetOutputBoundary outputBoundary) {
        this.dataAccess = dataAccess;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void save(SaveDataSetInputData inputData) {
        String id = inputData.getDatasetId();
        DataSet dataSet = inputData.getDataSet();

        if (id == null || id.isBlank()) {
            outputBoundary.present(new SaveDataSetOutputData(
                    id, false, "Dataset ID cannot be empty."
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
