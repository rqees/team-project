// src/main/java/interface_adapter/visualization/VisualizationPresenter.java
package interface_adapter.visualization;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import use_case.visualization.io.VisualizationOutputBoundary;
import use_case.visualization.io.VisualizationOutputData;
import use_case.visualization.model.Annotation;
import use_case.visualization.model.DataPoint;
import use_case.visualization.model.HeatmapModel;
import use_case.visualization.model.Matrix;
import use_case.visualization.model.PointPlotModel;
import use_case.visualization.model.VisualizationModel;

import java.util.List;

public class VisualizationPresenter implements VisualizationOutputBoundary {

    private final VisualizationViewModel viewModel;

    public VisualizationPresenter(VisualizationViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(VisualizationOutputData outputData) {
        VisualizationModel model = outputData.getVisualizationModel();

        if (model instanceof PointPlotModel pointModel) {
            XYChart chart = buildXYChartFromPointModel(pointModel);
            viewModel.setChart(chart);
            viewModel.setTitle(pointModel.getTitle());
            viewModel.setError(null);
        }
        else if (model instanceof HeatmapModel heatmapModel) {
            // For now, we just pass the matrix to the ViewModel.
            // You can later decide whether to use XChart's HeatMapChart
            // or render it as a table/colour grid in the View.
            Matrix matrix = heatmapModel.getMatrix();
            viewModel.setHeatmapMatrix(matrix);
            viewModel.setTitle(heatmapModel.getTitle());
            viewModel.setError(null);
        }
        else {
            // Fallback: unknown model type
            viewModel.setChart(null);
            viewModel.setHeatmapMatrix(null);
            viewModel.setTitle("Unsupported visualization type");
            viewModel.setError("Unsupported VisualizationModel implementation: " + model.getClass().getSimpleName());
        }
    }

    // =========================================================
    // Point-plot → XChart (SCATTER, LINE, BAR, HISTOGRAM)
    // =========================================================

    private XYChart buildXYChartFromPointModel(PointPlotModel model) {

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title(model.getTitle())
                .xAxisTitle(model.getXLabel())
                .yAxisTitle(model.getYLabel())
                .build();

        // Main data series
        addSeries(chart, "Data", model.getDataPoints());

        // Outliers, if any
        if (!model.getOutliers().isEmpty()) {
            addSeries(chart, "Outliers", model.getOutliers());
        }

        // Optional annotations (mean lines, thresholds, etc.)
        applyAnnotations(chart, model);

        return chart;
    }

    private void addSeries(XYChart chart, String seriesName, List<DataPoint> points) {
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];

        for (int i = 0; i < points.size(); i++) {
            x[i] = points.get(i).getX();
            y[i] = points.get(i).getY();
        }

        chart.addSeries(seriesName, x, y);
    }

    private void applyAnnotations(XYChart chart, PointPlotModel model) {
        List<DataPoint> points = model.getDataPoints();
        if (points.isEmpty()) {
            return;
        }

        double minX = points.stream().mapToDouble(DataPoint::getX).min().orElse(0.0);
        double maxX = points.stream().mapToDouble(DataPoint::getX).max().orElse(0.0);
        double minY = points.stream().mapToDouble(DataPoint::getY).min().orElse(0.0);
        double maxY = points.stream().mapToDouble(DataPoint::getY).max().orElse(0.0);

        int counter = 1;

        for (Annotation ann : model.getAnnotations()) {
            switch (ann.getType()) {
                case LINE_HORIZONTAL -> {
                    double[] xs = {minX, maxX};
                    double[] ys = {ann.getY(), ann.getY()};
                    chart.addSeries("hl-" + counter++ + " " + ann.getLabel(), xs, ys);
                }
                case LINE_VERTICAL -> {
                    double[] xs = {ann.getX(), ann.getX()};
                    double[] ys = {minY, maxY};
                    chart.addSeries("vl-" + counter++ + " " + ann.getLabel(), xs, ys);
                }
                case POINT -> {
                    double[] xs = {ann.getX()};
                    double[] ys = {ann.getY()};
                    chart.addSeries("ann-" + counter++ + " " + ann.getLabel(), xs, ys);
                }
            }
        }
    }
}
