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
        List<String> yCols = input.getYColumns();
        String colorByCol = input.getColorByColumn();
        
        // If no y-columns specified, choose one
        if (yCols.isEmpty()) {
            yCols = List.of(chooseYColumn(input, subsetData, xCol));
        }

        Map<String, List<Double>> numericColumns = subsetData.getNumericColumns();
        Map<String, List<String>> categoricalColumns = subsetData.getCategoricalColumns();
        
        // Handle X-axis: can be numeric or categorical
        List<Double> xVals;
        List<String> xCategoricalVals = null;
        boolean xIsCategorical = categoricalColumns.containsKey(xCol);
        
        if (xIsCategorical) {
            xCategoricalVals = categoricalColumns.get(xCol);
            // Convert categorical to numeric positions for plotting
            Map<String, Double> categoryToNumeric = new LinkedHashMap<>();
            double position = 0.0;
            for (String category : xCategoricalVals) {
                if (!categoryToNumeric.containsKey(category)) {
                    categoryToNumeric.put(category, position++);
                }
            }
            xVals = xCategoricalVals.stream()
                    .map(categoryToNumeric::get)
                    .collect(java.util.stream.Collectors.toList());
        } else {
            xVals = numericColumns.get(xCol);
            if (xVals == null) {
                throw new IllegalArgumentException("X column not found: " + xCol);
            }
        }

        // Get color grouping values if specified
        List<String> colorByVals = null;
        if (colorByCol != null && categoricalColumns.containsKey(colorByCol)) {
            colorByVals = categoricalColumns.get(colorByCol);
        }
        
        // Create data points for each y-column, grouped by color if specified
        Map<String, List<DataPoint>> yColumnDataPoints = new LinkedHashMap<>();
        List<DataPoint> firstYColumnPoints = null;
        
        for (String yCol : yCols) {
            List<Double> yVals = numericColumns.get(yCol);
            if (yVals == null) {
                throw new IllegalArgumentException("Y column not found: " + yCol);
            }
            
            int n = Math.min(xVals.size(), yVals.size());
            
            // If color grouping, create separate series for each group
            if (colorByVals != null && colorByVals.size() == n) {
                // Group by color category
                Map<String, List<DataPoint>> groupedPoints = new LinkedHashMap<>();
                
                for (int i = 0; i < n; i++) {
                    String group = colorByVals.get(i);
                    groupedPoints.computeIfAbsent(group, k -> new ArrayList<>())
                            .add(new DataPoint(
                                    xVals.get(i),
                                    yVals.get(i),
                                    Map.of("rowIndex", i, "yColumn", yCol, "group", group)
                            ));
                }
                
                // Create series name: "YColumn (Group)"
                for (Map.Entry<String, List<DataPoint>> entry : groupedPoints.entrySet()) {
                    String seriesName = yCol + " (" + entry.getKey() + ")";
                    yColumnDataPoints.put(seriesName, entry.getValue());
                    if (firstYColumnPoints == null) {
                        firstYColumnPoints = entry.getValue();
                    }
                }
            } else {
                // No grouping, single series
                List<DataPoint> dataPoints = new ArrayList<>(n);
                for (int i = 0; i < n; i++) {
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("rowIndex", i);
                    metadata.put("yColumn", yCol);
                    if (xCategoricalVals != null && i < xCategoricalVals.size()) {
                        metadata.put("xCategory", xCategoricalVals.get(i));
                    }
                    dataPoints.add(new DataPoint(xVals.get(i), yVals.get(i), metadata));
                }
                
                yColumnDataPoints.put(yCol, dataPoints);
                if (firstYColumnPoints == null) {
                    firstYColumnPoints = dataPoints;
                }
            }
        }
        
        // Use first y-column for backward compatibility
        String firstYCol = yCols.get(0);
        List<Double> firstYVals = numericColumns.get(firstYCol);

        // Build outlier points from OutlierSummaryMetric if present (using first y-column)
        List<DataPoint> outlierPoints = buildOutlierPoints(report, xVals, firstYVals);

        // Annotations (e.g., mean lines) can be added later
        List<Annotation> annotations = List.of();


        // Create y-label from all y-columns
        String yLabel = yCols.size() == 1 ? firstYCol : String.join(", ", yCols);

        return new PointPlotModel(
                input.getTitle(),
                kind,
                xCol,
                yLabel,
                firstYColumnPoints,
                yColumnDataPoints,
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
        if (report == null) {
            return null;
        }
        for (SummaryMetric metric : report.getSummaryMetrics()) {
            if (metric instanceof OutlierSummaryMetric outlierMetric &&
                    metric.getMetricType() == MetricType.OUTLIERS) {
                return outlierMetric;
            }
        }
        return null;
    }
}