package interface_adapter.visualization;

import org.knowm.xchart.XYChart;
import use_case.visualization.model.Matrix;

public class VisualizationViewModel {

    private XYChart chart;
    private Matrix heatmapMatrix;
    private String title;
    private String error;

    public XYChart getChart() { return chart; }
    public void setChart(XYChart chart) { this.chart = chart; }

    public Matrix getHeatmapMatrix() { return heatmapMatrix; }
    public void setHeatmapMatrix(Matrix heatmapMatrix) { this.heatmapMatrix = heatmapMatrix; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
