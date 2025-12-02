package use_case.load_csv;

public interface LoadOutputBoundary {
    void prepareFail(String errorMessage);
    void prepareSuccess();
}
