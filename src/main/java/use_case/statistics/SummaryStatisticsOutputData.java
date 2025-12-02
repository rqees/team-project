package use_case.statistics;

import entity.DataSubsetSpec;
import entity.SummaryMetric;
import entity.SummaryReport;
import java.util.List;


/**
 * Output data returned by the Summary Statistics Use Case Interactor.
 *
 * This object is passed to the Presenter through the Output Boundary.
 * It contains all computed summary metrics, in an entity-neutral,
 * presenter-ready form (but not formatted for UI).
 */
public class SummaryStatisticsOutputData {
    private final SummaryReport report;

    public SummaryStatisticsOutputData(SummaryReport report) {
        this.report = report;
    }

    public SummaryReport getReport() {
        return report;
    }

    // Convenience methods for accessing report data
    public int getSummaryId() {
        return report.getSummaryId();
    }

    public String getReportName() {
        return report.getReportName();
    }

    public DataSubsetSpec getSelectedSubset() {
        return report.getSelectedSubset();
    }

    public List<SummaryMetric> getMetrics() {
        return report.getSummaryMetrics();
    }
}
