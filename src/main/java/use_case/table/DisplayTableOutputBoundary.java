package use_case.table;

public interface DisplayTableOutputBoundary {
    void prepareSuccessView(DisplayTableOutputData outputData);
    void prepareFailureView(String errorMessage);
}