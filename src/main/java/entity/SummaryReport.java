package entity;

import java.util.List;

public class SummaryReport {
    private final int summaryId;
    private final String reportName;
    private final DataSubsetSpec selectedSubset;
    private final List<SummaryMetric> summaryMetrics;

    public SummaryReport(int summaryId, String reportName, DataSubsetSpec selectedSubset, List<SummaryMetric> summaryMetrics) {
        this.summaryId = summaryId;
        this.reportName = reportName;
        this.selectedSubset = selectedSubset;
        this.summaryMetrics = summaryMetrics;
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