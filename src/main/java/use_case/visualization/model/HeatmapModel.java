package use_case.visualization.model;

public class HeatmapModel implements VisualizationModel {

    private final String title;
    private final Matrix matrix;

    public HeatmapModel(String title, Matrix matrix) {
        this.title = title;
        this.matrix = matrix;
    }

    @Override public String getTitle() { return title; }
    @Override public PlotKind getPlotKind() { return PlotKind.HEATMAP; }

    public Matrix getMatrix() { return matrix; }
}
