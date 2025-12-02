package entity;

public class ScalarSummaryMetrics implements SummaryMetric{
    private final MetricType metricType;
    private final DataSubsetSpec selectedSubset;
    private final double metricValue;

    public ScalarSummaryMetrics(MetricType metricType, DataSubsetSpec selectedSubset, double metricValue) {
        this.metricType = metricType;
        this.selectedSubset = selectedSubset;
        this.metricValue = metricValue;
    }

    @Override
    public MetricType getMetricType() {
        return metricType;
    }

    @Override
    public DataSubsetSpec getSelectedSubset() {
        return selectedSubset;
    }

    public double getMetricValue() {
        return metricValue;
    }
}
