package entity;

public class SummaryMetrics {
    private final int summary_id;
    private final String metric_name;
    private final Column column_used;
    private final DataRow data_row_used;
    private final double metric_value;

    public SummaryMetrics(int summary_id, String metric_name, Column column_used, DataRow data_row_used, double metric_value) {
        this.summary_id = summary_id;
        this.metric_name = metric_name;
        this.column_used = column_used;
        this.data_row_used = data_row_used;
        this.metric_value = metric_value;
    }

    public double getMean() {
        return  metric_value;
    }
}
