package use_case.visualization;

import java.util.List;

/**
 * Response model for the "Get Available Visualizations" use case.
 * This will be passed to a presenter, then to a ViewModel.
 */
public class GetAvailableVisualizationsResponseModel {

    private final List<VisualizationType> allowedVisualizations;

    public GetAvailableVisualizationsResponseModel(List<VisualizationType> allowedVisualizations) {
        this.allowedVisualizations = allowedVisualizations;
    }

    /**
     * @return a list of visualization types that should be enabled in the UI
     */
    public List<VisualizationType> getAllowedVisualizations() {
        return allowedVisualizations;
    }
}
