package entity;

public class SummaryReport {
    private final int summary_id;
    private final String report_name;
    private final DataSet dataset;
    private final DataSubsetSpec selected_subset;

    public SummaryReport(int summary_id, String report_name, DataSet dataset, DataSubsetSpec selected_subset) {
        this.summary_id = summary_id;
        this.report_name = report_name;
        this.dataset = dataset;
        this.selected_subset = selected_subset;
    }
}
