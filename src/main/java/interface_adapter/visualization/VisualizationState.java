package interface_adapter.visualization;

import org.knowm.xchart.XYChart;
import use_case.visualization.model.Matrix;

 // Immutable snapshot of the visualization to display.
 // Fired by VisualizationViewModel via a "state" property change.
public class VisualizationState {

    private final XYChart xyChart;
    private final Matrix heatmapMatrix;
    private final String title;
    private final String errorMessage;

    public VisualizationState(XYChart xyChart,
                              Matrix heatmapMatrix,
                              String title,
                              String errorMessage) {
        this.xyChart = xyChart;
        this.heatmapMatrix = heatmapMatrix;
        this.title = title;
        this.errorMessage = errorMessage;
    }

    public XYChart getXyChart() {
        return xyChart;
    }

    public Matrix getHeatmapMatrix() {
        return heatmapMatrix;
    }

    public String getTitle() {
        return title;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasChart() {
        return xyChart != null;
    }

    public boolean hasHeatmap() {
        return heatmapMatrix != null;
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.isBlank();
    }
}

