package use_case.visualization;

import java.util.List;

/**
 * Request model for the "Get Available Visualizations" use case.
 * This is created by the controller based on the user's selection in the UI.
 *
 * The selection is represented as a list of column indices into the DataSet.
 */
public class GetAvailableVisualizationsRequestModel {

    private final String dataSetId;
    private final List<Integer> selectedColumnIndices;

    /**
     * @param dataSetId              identifier of the dataset (how your app tracks the current DataSet)
     * @param selectedColumnIndices  indices of the columns the user has selected for visualization
     */
    public GetAvailableVisualizationsRequestModel(String dataSetId,
                                                  List<Integer> selectedColumnIndices) {
        this.dataSetId = dataSetId;
        this.selectedColumnIndices = selectedColumnIndices;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    /**
     * @return the indices of the selected columns in the DataSet
     */
    public List<Integer> getSelectedColumnIndices() {
        return selectedColumnIndices;
    }
}
