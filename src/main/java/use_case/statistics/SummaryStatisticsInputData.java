package use_case.statistics;

import entity.DataSubsetSpec;

/**
 * The input data for the Summary Statistics Use Case.
 */
public class SummaryStatisticsInputData {
    private int dataSubsetId;
    private String reportName;
    private DataSubsetSpec dataSubsetSpec;

    public int getDataSubsetId() {
        return dataSubsetId;
    }

    public DataSubsetSpec getDataSubsetSpec() {
        return dataSubsetSpec;
    }

    public String getReportName() {
        return reportName;
    }
}
