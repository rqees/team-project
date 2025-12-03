package use_case.load_api;

public interface LoadApiInputBoundary {
    /**
     * Executes the Load API use case.
     * @param loadApiInputData input data for the use case
     */
    void execute(LoadApiInputData loadApiInputData);
}
