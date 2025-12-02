package use_case.statistics;

import entity.DataSubsetSpec;

/**
 * The input data for the Summary Statistics Use Case.
 */
public class SummaryStatisticsInputData {
    private final int dataSubsetId;
    private final String reportName;
    private final DataSubsetSpec dataSubsetSpec;

    /**
     * Constructs input data for the Summary Statistics use case.
     *
     * @param dataSubsetId unique identifier for this data subset
     * @param reportName name of the report to be generated
     * @param dataSubsetSpec specification of which data to analyze
     */
    public SummaryStatisticsInputData(int dataSubsetId, String reportName, DataSubsetSpec dataSubsetSpec) {
        this.dataSubsetId = dataSubsetId;
        this.reportName = reportName;
        this.dataSubsetSpec = dataSubsetSpec;
    }

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
