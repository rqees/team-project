package use_case.save_dataset;

/**
 * Output boundary for presenting save dataset results.
 */
public interface SaveDataSetOutputBoundary {
    /**
     * Presents the outcome of a save operation.
     *
     * @param outputData output model describing success/failure
     */
    void present(final SaveDataSetOutputData outputData);
}
