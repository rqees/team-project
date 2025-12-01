// src/main/java/interface_adapter/visualization/VisualizationPresenter.java
package interface_adapter.visualization;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import use_case.visualization.io.VisualizationOutputBoundary;
import use_case.visualization.io.VisualizationOutputData;
import use_case.visualization.model.Annotation;
import use_case.visualization.model.DataPoint;
import use_case.visualization.model.HeatmapModel;
import use_case.visualization.model.Matrix;
import use_case.visualization.model.PlotKind;
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

            VisualizationState state = new VisualizationState(
                    chart,
                    null,
                    pointModel.getTitle(),
                    null
            );
            viewModel.setState(state);
        }
        else if (model instanceof HeatmapModel heatmapModel) {
            Matrix matrix = heatmapModel.getMatrix();

            VisualizationState state = new VisualizationState(
                    null,
                    matrix,
                    heatmapModel.getTitle(),
                    null
            );
            viewModel.setState(state);
        }
        else {
            VisualizationState state = new VisualizationState(
                    null, null,
                    "Unsupported visualization",
                    "Unsupported VisualizationModel implementation: " + model.getClass().getSimpleName()
            );
            viewModel.setState(state);
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
        addSeries(chart, "Data", model.getDataPoints(), model.getPlotKind());

        // Outliers, if any
        if (!model.getOutliers().isEmpty()) {
            addSeries(chart, "Outliers", model.getOutliers(), PlotKind.SCATTER);
        }

        // Optional annotations (mean lines, thresholds, etc.)
        applyAnnotations(chart, model);

        return chart;
    }

    private void addSeries(XYChart chart, String seriesName, List<DataPoint> points, PlotKind plotKind) {
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];

        for (int i = 0; i < points.size(); i++) {
            x[i] = points.get(i).getX();
            y[i] = points.get(i).getY();
        }

        XYSeries series = chart.addSeries(seriesName, x, y);
        
        // Set the render style based on PlotKind
        // XChart XYSeriesRenderStyle enum - using valueOf to handle version differences
        switch (plotKind) {
            case BAR -> {
                // Option A: treat BAR like Area (or Line/Scatter) for now
                series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
                series.setMarker(null);
            }
            case LINE -> {
                series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            }
            case HISTOGRAM -> {
                series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.StepArea);
            }
            case SCATTER -> {
                series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            }
            default -> {
                series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            }
        }
        
        
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
