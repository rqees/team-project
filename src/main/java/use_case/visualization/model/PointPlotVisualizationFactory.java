package use_case.visualization.model;

import entity.*;
import use_case.visualization.data.DataSubsetData;
import use_case.visualization.io.*;


import java.util.*;

public class PointPlotVisualizationFactory implements VisualizationModelFactory {

    @Override
    public VisualizationModel createModel(VisualizationInputData input,
                                          DataSubsetData subsetData,
                                          SummaryReport report) {

        PlotKind kind = input.getPlotKind(); // SCATTER, LINE, BAR, HISTOGRAM

        String xCol = chooseXColumn(input, subsetData);
        String yCol = chooseYColumn(input, subsetData, xCol);

        Map<String, List<Double>> numericColumns = subsetData.getNumericColumns();
        List<Double> xVals = numericColumns.get(xCol);
        List<Double> yVals = numericColumns.get(yCol);

        if (xVals == null || yVals == null) {
            throw new IllegalArgumentException("Selected columns not found in numeric subset data: x=" + xCol + ", y=" + yCol);
        }

        int n = Math.min(xVals.size(), yVals.size());
        List<DataPoint> dataPoints = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            dataPoints.add(new DataPoint(
                    xVals.get(i),
                    yVals.get(i),
                    Map.of("rowIndex", i)
            ));
        }

        // Build outlier points from OutlierSummaryMetric if present
        List<DataPoint> outlierPoints = buildOutlierPoints(report, xVals, yVals);

        // Annotations (e.g., mean lines) can be added later
        List<Annotation> annotations = List.of();

        return new PointPlotModel(
                input.getTitle(),
                kind,
                xCol,
                yCol,
                dataPoints,
                outlierPoints,
                annotations
        );
    }

    private String chooseXColumn(VisualizationInputData input, DataSubsetData subsetData) {
        List<String> xCols = input.getXColumns();
        if (!xCols.isEmpty()) {
            return xCols.get(0);
        }
        return subsetData.getNumericColumns().keySet().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No numeric columns available for X-axis"));
    }

    private String chooseYColumn(VisualizationInputData input,
                                 DataSubsetData subsetData,
                                 String xCol) {
        List<String> yCols = input.getYColumns();
        if (!yCols.isEmpty()) {
            return yCols.get(0);
        }
        return subsetData.getNumericColumns().keySet().stream()
                .filter(col -> !col.equals(xCol))
                .findFirst()
                .orElse(xCol);
    }

    private List<DataPoint> buildOutlierPoints(SummaryReport report,
                                               List<Double> xVals,
                                               List<Double> yVals) {
        int n = Math.min(xVals.size(), yVals.size());
        List<DataPoint> outlierPoints = new ArrayList<>();

        OutlierSummaryMetric outlierMetric = findOutlierMetric(report);
        if (outlierMetric == null) {
            return outlierPoints;
        }

        for (OutlierPoint p : outlierMetric.getOutlier_points()) {
            int rowIndex = p.getRowIndex();
            if (rowIndex >= 0 && rowIndex < n) {
                outlierPoints.add(new DataPoint(
                        xVals.get(rowIndex),
                        yVals.get(rowIndex),
                        Map.of(
                                "rowIndex", rowIndex,
                                "zScore", p.getZScore()
                        )
                ));
            }
        }
        return outlierPoints;
    }

    private OutlierSummaryMetric findOutlierMetric(SummaryReport report) {
        for (SummaryMetric metric : report.getSummaryMetrics()) {
            if (metric instanceof OutlierSummaryMetric outlierMetric &&
                    metric.getMetricType() == MetricType.OUTLIERS) {
                return outlierMetric;
            }
        }
        return null;
    }
}