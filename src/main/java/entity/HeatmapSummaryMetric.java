package entity;

import  java.util.List;

public class HeatmapSummaryMetric implements SummaryMetric {
    private final MetricType metric_type;
    private final DataSubsetSpec selected_subset;
    private final double[][] heatmap_values;
    private final List<String> rowLabels;
    private final List<String> colLabels;

    public HeatmapSummaryMetric(MetricType metric_type,
                                DataSubsetSpec selected_subset,
                                double[][] heatmap_values,
                                List<String> rowLabels,
                                List<String> colLabels) {
        this.metric_type = metric_type;
        this.selected_subset = selected_subset;
        this.heatmap_values = heatmap_values;
        this.rowLabels = List.copyOf(rowLabels);
        this.colLabels = List.copyOf(colLabels);
    }

    @Override
    public MetricType getMetricType() {
        return metric_type;
    }

    @Override
    public DataSubsetSpec getSelected_subset() {
        return selected_subset;
    }

    public double[][] getHeatmap_values() {
        return heatmap_values;
    }

    public List<String> getRowLabels() {
        return rowLabels;
    }

    public List<String> getColLabels() {
        return colLabels;
    }
}

