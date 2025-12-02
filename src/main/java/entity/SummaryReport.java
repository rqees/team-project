package entity;

import java.util.List;

public class SummaryReport {
    private final int summaryId;
    private final String reportName;
    private final DataSubsetSpec selectedSubset;
    private final List<SummaryMetric> summaryMetrics;

    public SummaryReport(int summary_id, String report_name, DataSubsetSpec selected_subset,  List<SummaryMetric> summary_metrics) {
        this.summaryId = summary_id;
        this.reportName = report_name;
        this.selectedSubset = selected_subset;
        this.summaryMetrics = summary_metrics;
    }

    public int getSummaryId() {
        return summaryId;
    }

    public String getReportName() {
        return reportName;
    }

    public DataSubsetSpec getSelectedSubset() {
        return selectedSubset;
    }

    public List<SummaryMetric> getSummaryMetrics() {
        return summaryMetrics;
    }
}