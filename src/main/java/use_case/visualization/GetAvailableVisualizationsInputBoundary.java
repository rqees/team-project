package use_case.visualization;

/**
 * Input boundary for the "Get Available Visualizations" use case.
 * The controller will call this interface.
 */
public interface GetAvailableVisualizationsInputBoundary {

    /**
     * Determine which visualization types are allowed for the given dataset
     * and selected columns.
     *
     * @param requestModel input data describing the dataset and selected columns
     * @return a response model containing the allowed visualization types
     */
    GetAvailableVisualizationsResponseModel getAvailableVisualizations(
            GetAvailableVisualizationsRequestModel requestModel
    );
}
