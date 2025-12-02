package entity;

import java.util.List;

public class OutlierSummaryMetric implements SummaryMetric {
    private final MetricType metric_type;
    private final DataSubsetSpec selected_subset;
    private final double outlier_z_score_threshold;  // e.g. std dev or threshold, or just a scalar summary
    private final List<OutlierPoint> outlier_points;

    public OutlierSummaryMetric(MetricType metric_type,
                                DataSubsetSpec selected_subset,
                                double metric_value,
                                List<OutlierPoint> outlier_points) {
        this.metric_type = metric_type;
        this.selected_subset = selected_subset;
        this.outlier_z_score_threshold = metric_value;
        this.outlier_points = List.copyOf(outlier_points);
    }

    @Override
    public MetricType getMetricType() {
        return metric_type;
    }

    @Override
    public DataSubsetSpec getSelectedSubset() {
        return selected_subset;
    }

    public double getOutlier_z_score_threshold() {
        return outlier_z_score_threshold;
    }

    public List<OutlierPoint> getOutlier_points() {
        return outlier_points;
    }

}
