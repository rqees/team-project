// src/main/java/interface_adapter/visualization/VisualizationPresenter.java
package interface_adapter.visualization;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import use_case.visualization.io.VisualizationOutputBoundary;
import use_case.visualization.io.VisualizationOutputData;
import use_case.visualization.model.Annotation;
import use_case.visualization.model.DataPoint;
import use_case.visualization.model.HeatmapModel;
import use_case.visualization.model.Matrix;
import use_case.visualization.model.PlotKind;
import use_case.visualization.model.PointPlotModel;
import use_case.visualization.model.VisualizationModel;

import java.awt.*;
import java.util.List;
import java.util.Map;

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
        // Modern dark theme colors
        Color bgDark = new Color(30, 30, 35);
        Color bgMedium = new Color(40, 40, 45);
        Color gridColor = new Color(60, 60, 65);

        XYChart chart = new XYChartBuilder()
                .width(900)
                .height(600)
                .title(model.getTitle())
                .xAxisTitle(model.getXLabel())
                .yAxisTitle(model.getYLabel())
                .theme(Styler.ChartTheme.Matlab)
                .build();
        
        // Apply modern dark theme styling
        org.knowm.xchart.style.XYStyler styler = chart.getStyler();
        styler.setChartBackgroundColor(bgDark);
        styler.setPlotBackgroundColor(bgDark);
        styler.setPlotBorderColor(gridColor);
        styler.setPlotGridLinesColor(gridColor);
        styler.setChartTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        styler.setChartTitleBoxBackgroundColor(bgMedium);
        styler.setChartTitleBoxBorderColor(gridColor);
        styler.setAxisTitleFont(new Font("Segoe UI", Font.PLAIN, 12));
        styler.setAxisTickLabelsFont(new Font("Segoe UI", Font.PLAIN, 10));
        styler.setLegendFont(new Font("Segoe UI", Font.PLAIN, 11));
        styler.setLegendBackgroundColor(bgMedium);
        styler.setLegendBorderColor(gridColor);
        styler.setLegendPosition(Styler.LegendPosition.OutsideE);
        styler.setPlotGridLinesVisible(true);
        styler.setPlotGridLinesStroke(new BasicStroke(0.5f));
        styler.setPlotBorderVisible(true);
        styler.setAntiAlias(true);

        // Modern sophisticated color palette (vibrant, high contrast for dark theme)
        Color[] modernColors = new Color[]{
            new Color(100, 150, 255),  // Bright Blue
            new Color(255, 120, 80),   // Coral/Orange
            new Color(80, 220, 120),   // Bright Green
            new Color(255, 100, 150),  // Pink
            new Color(180, 120, 255),  // Purple
            new Color(255, 200, 80),   // Gold
            new Color(80, 220, 220),   // Cyan
            new Color(255, 150, 100),  // Peach
            new Color(150, 200, 255),  // Light Blue
            new Color(220, 180, 255)   // Lavender
        };
        
        // Add series for each y-column with different colors
        Map<String, List<DataPoint>> yColumnDataPoints = model.getYColumnDataPoints();
        if (yColumnDataPoints != null && !yColumnDataPoints.isEmpty()) {
            int colorIndex = 0;
            
            for (Map.Entry<String, List<DataPoint>> entry : yColumnDataPoints.entrySet()) {
                String yColumnName = entry.getKey();
                List<DataPoint> points = entry.getValue();
                Color seriesColor = modernColors[colorIndex % modernColors.length];
                addSeries(chart, yColumnName, points, model.getPlotKind(), seriesColor);
                colorIndex++;
            }
        } else {
            // Fallback to single series for backward compatibility
            addSeries(chart, "Data", model.getDataPoints(), model.getPlotKind(), modernColors[0]);
        }

        // Outliers, if any
        if (!model.getOutliers().isEmpty()) {
            addSeries(chart, "Outliers", model.getOutliers(), PlotKind.SCATTER, new Color(255, 80, 80));
        }

        // Optional annotations (mean lines, thresholds, etc.)
        applyAnnotations(chart, model);

        return chart;
    }

    private void addSeries(XYChart chart, String seriesName, List<DataPoint> points, PlotKind plotKind, Color color) {
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];

        for (int i = 0; i < points.size(); i++) {
            x[i] = points.get(i).getX();
            y[i] = points.get(i).getY();
        }

        XYSeries series = chart.addSeries(seriesName, x, y);
        
        // Set color if specified
        if (color != null) {
            series.setLineColor(color);
            series.setMarkerColor(color);
            series.setFillColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30)); // Semi-transparent fill
        }
        
        // Set the render style based on PlotKind with modern styling
        switch (plotKind) {
            case BAR -> {
                series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
                series.setMarker(null);
                series.setLineWidth(2);
                if (color != null) {
                    series.setFillColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 180));
                }
            }
            case LINE -> {
                series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
                series.setLineWidth(2.5f);
                series.setMarker(null);
            }
            case HISTOGRAM -> {
                series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.StepArea);
                series.setLineWidth(2);
                if (color != null) {
                    series.setFillColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
                }
            }
            case SCATTER -> {
                series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
                // Scatter plots use markers by default
                series.setLineWidth(0); // No line for scatter
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
