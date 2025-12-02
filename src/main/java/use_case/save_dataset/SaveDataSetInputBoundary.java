package use_case.save_dataset;

/**
 * Input boundary for requesting a dataset save operation.
 */
public interface SaveDataSetInputBoundary {
    /**
     * Executes the save dataset use case.
     *
     * @param inputData input model containing the dataset identifier
     */
    void execute(SaveDataSetInputData inputData) throws java.io.IOException;
}
