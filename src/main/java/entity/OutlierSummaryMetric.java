package entity;

import java.util.List;

public class OutlierSummaryMetric implements SummaryMetric {
    private final MetricType metric_type;
    private final DataSubsetSpec selected_subset;
    private final double metric_value;  // e.g. std dev or threshold, or just a scalar summary
    private final List<OutlierPoint> outlier_points;

    public OutlierSummaryMetric(MetricType metric_type,
                                DataSubsetSpec selected_subset,
                                double metric_value,
                                List<OutlierPoint> outlier_points) {
        this.metric_type = metric_type;
        this.selected_subset = selected_subset;
        this.metric_value = metric_value;
        this.outlier_points = List.copyOf(outlier_points);
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

    public List<OutlierPoint> getOutlier_points() {
        return outlier_points;
    }

}
