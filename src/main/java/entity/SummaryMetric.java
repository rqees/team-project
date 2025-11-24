package entity;

import use_case.statistics.MetricType;

public class SummaryMetric {
    private final MetricType metric_type;
    private final DataSubsetSpec selected_subset;
    private final double metric_value;

    public SummaryMetric(MetricType metrictype, DataSubsetSpec selected_subset, double metric_value) {
        this.metric_type = metrictype;
        this.selected_subset = selected_subset;
        this.metric_value = metric_value;
    }

    public double getMetric_value() {
        return metric_value;
    }

    public MetricType getMetric_type() {
        return metric_type;
    }

    public DataSubsetSpec getSelected_subset() {
        return selected_subset;
    }
}
