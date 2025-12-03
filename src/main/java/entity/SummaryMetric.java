package entity;

/**
 * Summary Metric entity for summary statistics use case.
 */
public interface SummaryMetric {
    MetricType getMetricType();
    DataSubsetSpec getSelectedSubset();
}
