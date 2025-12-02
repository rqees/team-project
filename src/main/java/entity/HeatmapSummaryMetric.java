package entity;

import  java.util.List;

public class HeatmapSummaryMetric implements SummaryMetric {
    private final MetricType metricType;
    private final DataSubsetSpec selectedSubset;
    private final double[][] heatmapValues;
    private final List<String> rowLabels;
    private final List<String> colLabels;

    public HeatmapSummaryMetric(MetricType metric_type,
                                DataSubsetSpec selected_subset,
                                double[][] heatmap_values,
                                List<String> rowLabels,
                                List<String> colLabels) {
        this.metricType = metric_type;
        this.selectedSubset = selected_subset;
        this.heatmapValues = heatmap_values;
        this.rowLabels = List.copyOf(rowLabels);
        this.colLabels = List.copyOf(colLabels);
    }

    @Override
    public MetricType getMetricType() {
        return metricType;
    }

    @Override
    public DataSubsetSpec getSelectedSubset() {
        return selectedSubset;
    }

    public double[][] getHeatmapValues() {
        return heatmapValues;
    }

    public List<String> getRowLabels() {
        return rowLabels;
    }

    public List<String> getColLabels() {
        return colLabels;
    }
}

