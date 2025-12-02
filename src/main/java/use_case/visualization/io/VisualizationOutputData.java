package use_case.visualization.io;
import use_case.visualization.model.VisualizationModel;

public class VisualizationOutputData {
    private final VisualizationModel visualizationModel;

    public VisualizationOutputData(VisualizationModel visualizationModel) {
        this.visualizationModel = visualizationModel;
    }

    public VisualizationModel getVisualizationModel() {
        return visualizationModel;
    }
}