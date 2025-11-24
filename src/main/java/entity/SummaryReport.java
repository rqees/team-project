package entity;

import java.util.List;

public class SummaryReport {
    private final int summary_id;
    private final String report_name;
    private final DataSubsetSpec selected_subset;
    private final List<SummaryMetric> summary_metrics;

    public SummaryReport(int summary_id, String report_name, DataSubsetSpec selected_subset,  List<SummaryMetric> summary_metrics) {
        this.summary_id = summary_id;
        this.report_name = report_name;
        this.selected_subset = selected_subset;
        this.summary_metrics = summary_metrics;
    }

    public int getSummary_id() {
        return summary_id;
    }

    public String getReport_name() {
        return report_name;
    }

    public DataSubsetSpec getSelected_subset() {
        return selected_subset;
    }

    public List<SummaryMetric> getSummary_metrics() {
        return summary_metrics;
    }
}
