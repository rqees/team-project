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

    public double getMean() {
        return  metric_value;
    }
}
