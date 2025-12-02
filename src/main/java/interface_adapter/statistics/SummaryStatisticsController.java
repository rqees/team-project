package interface_adapter.statistics;

import use_case.statistics.SummaryStatisticsInputBoundary;
import use_case.statistics.SummaryStatisticsInputData;

import java.util.List;

/**
 * Controller for Summary Statistics (Clean Architecture compliant).
 *
 * Clean Architecture Compliance:
 * - NO entity imports (Interface Adapter must not depend on entities)
 * - Only depends on Use Case layer (inward dependency)
 * - Passes primitives to InputData, which creates entities internally
 */
public class SummaryStatisticsController {

    private final SummaryStatisticsInputBoundary interactor;

    public SummaryStatisticsController(SummaryStatisticsInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the summary statistics use case.
     *
     * Accepts primitive types from the UI and passes to InputData.
     * InputData will create entities internally (Clean Architecture compliant).
     *
     * @param dataSubsetId unique identifier for this analysis
     * @param reportName name for the generated report
     * @param datasetId identifier of the dataset to analyze
     * @param columnNames list of column names to include in analysis
     * @param rowIndices list of row indices to include in analysis
     */
    public void calculateStatistics(
            int dataSubsetId,
            String reportName,
            String datasetId,
            List<String> columnNames,
            List<Integer> rowIndices) {

        try {
            // Create input data with primitives
            // InputData creates entities internally - Controller never touches entities
            SummaryStatisticsInputData inputData = new SummaryStatisticsInputData(
                    dataSubsetId,
                    reportName,
                    datasetId,      // Primitive
                    columnNames,    // Primitive
                    rowIndices      // Primitive
            );

            // Execute the use case
            interactor.execute(inputData);

        } catch (IllegalArgumentException e) {
            System.err.println("Controller validation error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Executes the summary statistics use case with all rows.
     *
     * Convenience method when analyzing all rows in the dataset.
     *
     * @param dataSubsetId unique identifier for this analysis
     * @param reportName name for the generated report
     * @param datasetId identifier of the dataset to analyze
     * @param columnNames list of column names to include in analysis
     * @param totalRows total number of rows in the dataset
     */
    public void calculateStatisticsAllRows(
            int dataSubsetId,
            String reportName,
            String datasetId,
            List<String> columnNames,
            int totalRows) {

        // Create list of all row indices
        List<Integer> allRowIndices = new java.util.ArrayList<>();
        for (int i = 0; i < totalRows; i++) {
            allRowIndices.add(i);
        }

        calculateStatistics(dataSubsetId, reportName, datasetId, columnNames, allRowIndices);
    }
}