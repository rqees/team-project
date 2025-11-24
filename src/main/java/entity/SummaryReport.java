package entity;

public class SummaryReport {
    private final int summary_id;
    private final String report_name;
    private final DataSubsetSpec selected_subset;

    public SummaryReport(int summary_id, String report_name, DataSubsetSpec selected_subset) {
        this.summary_id = summary_id;
        this.report_name = report_name;
        this.selected_subset = selected_subset;
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
}
