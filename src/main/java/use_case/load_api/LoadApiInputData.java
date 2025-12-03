package use_case.load_api;

public class LoadApiInputData {
    private final String datasetName;

    public LoadApiInputData(String datasetName) {
        this.datasetName = datasetName;
    }

    String getDatasetName() {
        return datasetName;
    }
}
