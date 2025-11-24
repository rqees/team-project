package entity;

public class SummaryMetrics {
    private final int summary_id;
    private final String metric_name;
    private final DataSubsetSpec selected_subset;
    private final double metric_value;

    public SummaryMetrics(int summary_id, String metric_name, DataSubsetSpec selected_subset, double metric_value) {
        this.summary_id = summary_id;
        this.metric_name = metric_name;
        this.selected_subset = selected_subset;
        this.metric_value = metric_value;
    }

    public double getMetric_value() {
        return metric_value;
    }

    public int getSummary_id() {
        return summary_id;
    }

    public String getMetric_name() {
        return metric_name;
    }

    public DataSubsetSpec getSelected_subset() {
        return selected_subset;
    }
}
