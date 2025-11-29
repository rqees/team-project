package entity;

public class ScalarSummaryMetrics implements SummaryMetric{
    private final MetricType metric_type;
    private final DataSubsetSpec selected_subset;
    private final double metric_value;

    public ScalarSummaryMetrics(MetricType metricType, DataSubsetSpec selectedSubset, double metricValue) {
        metric_type = metricType;
        selected_subset = selectedSubset;
        metric_value = metricValue;
    }

    @Override
    public MetricType getMetricType() {
        return metric_type;
    }

    @Override
    public DataSubsetSpec getSelected_subset() {
        return selected_subset;
    }

    public double getMetric_value() {
        return metric_value;
    }
}
