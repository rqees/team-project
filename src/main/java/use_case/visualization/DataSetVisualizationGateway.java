package use_case.visualization;

import entity.DataSet;

/**
 * Gateway interface for accessing DataSet objects from the visualization use cases.
 * The implementation will live in the interface_adapter / data access layer.
 */
public interface DataSetVisualizationGateway {

    /**
     * @param dataSetId identifier of the dataset to retrieve
     * @return the DataSet associated with the given id
     */
    DataSet getDataSetById(String dataSetId);
}
