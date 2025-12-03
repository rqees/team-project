package use_case.load_csv;

public interface LoadInputBoundary {
    /**
     * Executes the Load CSV use case.
     * @param loadInputData input data for the use case
     */
    void execute(LoadInputData loadInputData);
}
