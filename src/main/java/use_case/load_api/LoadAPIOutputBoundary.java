package use_case.load_api;

public interface LoadAPIOutputBoundary {
    void prepareFail(String errorMessage);
    void prepareSuccess();
}
