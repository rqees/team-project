package use_case.load_api;

public interface LoadApiDataGateway {
    /**
     * Gets the CSV from the CKAN API as a full string.
     * @param datasetName name of the dataset
     * @return full csv
     */
    String getCsv(String datasetName);
}
