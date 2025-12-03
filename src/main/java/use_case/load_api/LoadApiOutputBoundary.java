package use_case.load_api;

public interface LoadApiOutputBoundary {
    /**
     * Prepares to update the view model with a failure.
     * @param errorMessage error message
     */
    void prepareFail(String errorMessage);

    /**
     * Prepares to update the view model with a Success.
     */
    void prepareSuccess();
}
