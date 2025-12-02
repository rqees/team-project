
package interface_adapter.visualization;

import use_case.visualization.io.VisualizationInputBoundary;
import use_case.visualization.io.VisualizationInputData;

/**
 * Controller for the Visualization use case.
 * The View calls this with the chosen subset, plot type, etc.
 */
public class VisualizationController {

    private final VisualizationInputBoundary interactor;

    public VisualizationController(VisualizationInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void visualize(VisualizationInputData inputData) {
        interactor.visualize(inputData);
    }
}

