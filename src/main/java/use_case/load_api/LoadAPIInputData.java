package use_case.load_api;

public class LoadAPIInputData {
    private final String datasetName;

    public LoadAPIInputData(String datasetName) {
        this.datasetName = datasetName;
    }

    String getDatasetName() {
        return datasetName;
    }
}
