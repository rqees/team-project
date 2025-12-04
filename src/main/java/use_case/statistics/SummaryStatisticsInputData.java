package use_case.statistics;

import java.util.List;

import entity.DataSubsetSpec;

/**
 * Input data for the Summary Statistics Use Case.
 */
public class SummaryStatisticsInputData {
    private final int dataSubsetId;
    private final String reportName;
    private final DataSubsetSpec dataSubsetSpec;

    /**
     * Primary constructor - accepts primitives from Controller.
     *
     * @param dataSubsetId unique identifier for this data subset
     * @param reportName name of the report to be generated
     * @param datasetId dataset identifier (primitive)
     * @param columnNames columns to analyze (primitive)
     * @param rowIndices rows to include (primitive)
     * @throws IllegalArgumentException if any required parameter is null or invalid
     */
    public SummaryStatisticsInputData(
            final int dataSubsetId,
            final String reportName,
            final String datasetId,
            final List<String> columnNames,
            final List<Integer> rowIndices) {

        // Validation
        if (reportName == null || reportName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Report name cannot be null or empty");
        }
        if (datasetId == null || datasetId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Dataset ID cannot be empty");
        }
        if (columnNames == null || columnNames.isEmpty()) {
            throw new IllegalArgumentException(
                    "Column names cannot be empty");
        }
        if (rowIndices == null || rowIndices.isEmpty()) {
            throw new IllegalArgumentException(
                    "Row indices cannot be empty");
        }

        this.dataSubsetId = dataSubsetId;
        this.reportName = reportName;
        // Create entity internally - Controller doesn't need to know about entities
        this.dataSubsetSpec = new DataSubsetSpec(datasetId, columnNames, rowIndices);
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
