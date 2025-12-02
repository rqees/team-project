package use_case.statistics;

import entity.*;
import use_case.visualization.gateway.DataSubsetGateway;
import use_case.visualization.gateway.SummaryReportGateway;
import use_case.visualization.data.DataSubsetData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Summary Statistics Interactor
 * RESPONSIBILITY: Orchestrate the use case flow only.
 * All calculations are delegated to StatisticsCalculator.
 */
public class SummaryStatisticsInteractor implements SummaryStatisticsInputBoundary {

    private final DataSubsetGateway dataSubsetGateway;
    private final SummaryReportGateway summaryReportGateway;
    private final SummaryStatisticsOutputBoundary summaryStatisticsPresenter;
    private static final double OUTLIER_Z_SCORE_THRESHOLD = 3.0;

    public SummaryStatisticsInteractor(DataSubsetGateway dataSubsetGateway,
                                       SummaryReportGateway summaryReportGateway,
                                       SummaryStatisticsOutputBoundary presenter) {
        this.dataSubsetGateway = dataSubsetGateway;
        this.summaryReportGateway = summaryReportGateway;
        this.summaryStatisticsPresenter = presenter;
    }

    @Override
    public void execute(SummaryStatisticsInputData input) {
        try {
            // Step 1: Validate input
            validateInput(input);

            final DataSubsetSpec subset = input.getDataSubsetSpec();

            // Step 2: Load the data subset
            DataSubsetData subsetData;
            try {
                subsetData = dataSubsetGateway.loadSubset(subset);
            } catch (IllegalStateException e) {
                summaryStatisticsPresenter.prepareFailView("Dataset not loaded: " + e.getMessage());
                return;
            } catch (Exception e) {
                summaryStatisticsPresenter.prepareFailView("Error loading data subset: " + e.getMessage());
                return;
            }

            // Step 3: Verify we have numeric data
            if (subsetData.getNumericColumns().isEmpty()) {
                summaryStatisticsPresenter.prepareFailView("No numeric columns found for statistical analysis");
                return;
            }

            // Step 4: Calculate all summary metrics
            List<SummaryMetric> metrics = calculateAllMetrics(subset, subsetData);

            // Step 5: Create SummaryReport entity
            SummaryReport report = new SummaryReport(
                    input.getDataSubsetId(),
                    input.getReportName(),
                    subset,
                    metrics
            );

            // Step 6: Save the report
            summaryReportGateway.save(report);

            // Step 7: Create and send output data
            SummaryStatisticsOutputData outputData = new SummaryStatisticsOutputData(report);
            summaryStatisticsPresenter.prepareSuccessView(outputData);

        } catch (IllegalArgumentException e) {
            summaryStatisticsPresenter.prepareFailView("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            summaryStatisticsPresenter.prepareFailView("Error computing statistics: " + e.getMessage());
        }
    }

    // ========================================
    // VALIDATION (Application Logic)
    // ========================================

    private void validateInput(SummaryStatisticsInputData input) {
        if (input == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }

        if (input.getDataSubsetSpec() == null) {
            throw new IllegalArgumentException("Data subset specification is required");
        }

        if (input.getDataSubsetSpec().getColumnNames() == null ||
                input.getDataSubsetSpec().getColumnNames().isEmpty()) {
            throw new IllegalArgumentException("No columns specified for analysis");
        }

        if (input.getReportName() == null || input.getReportName().trim().isEmpty()) {
            throw new IllegalArgumentException("Report name is required");
        }
    }

    // ========================================
    // ORCHESTRATION (Application Logic)
    // ========================================

    private List<SummaryMetric> calculateAllMetrics(DataSubsetSpec subset, DataSubsetData subsetData) {
        List<SummaryMetric> metrics = new ArrayList<>();

        Map<String, List<Double>> numericColumns = subsetData.getNumericColumns();
        List<String> columnNames = new ArrayList<>(numericColumns.keySet());

        // Calculate metrics for each numeric column
        for (String columnName : columnNames) {
            metrics.addAll(calculateColumnMetrics(subset, columnName, numericColumns.get(columnName)));
        }

        // Calculate outliers across all numeric columns
        List<OutlierPoint> outliers = detectOutliers(subset, numericColumns);
        if (!outliers.isEmpty()) {
            metrics.add(new OutlierSummaryMetric(
                    MetricType.OUTLIERS,
                    subset,
                    OUTLIER_Z_SCORE_THRESHOLD,
                    outliers
            ));
        }

        // Calculate correlation matrix if multiple numeric columns exist
        if (columnNames.size() > 1) {
            double[][] correlationMatrix = calculateCorrelationMatrix(numericColumns, columnNames);
            metrics.add(new CorrelationMatrixMetric(
                    MetricType.CORRELATION_MATRIX,
                    subset,
                    correlationMatrix,
                    columnNames
            ));
        }

        return metrics;
    }

    private List<SummaryMetric> calculateColumnMetrics(DataSubsetSpec subset, String columnName, List<Double> values) {
        List<SummaryMetric> metrics = new ArrayList<>();
        DataSubsetSpec columnSubset = createColumnSubset(subset, columnName);

        try {
            if (values == null || values.isEmpty()) {
                return metrics;
            }

            // Check if there are any non-null values
            long nonNullCount = StatisticsCalculator.countNonNull(values);
            if (nonNullCount == 0) {
                // All values are null - skip this column or report as no data
                System.err.println("Warning: Column '" + columnName + "' has no valid numeric values");
                return metrics;
            }

            // DELEGATE ALL CALCULATIONS TO StatisticsCalculator
            double mean = StatisticsCalculator.calculateMean(values);
            double median = StatisticsCalculator.calculateMedian(values);
            double stdDev = StatisticsCalculator.calculateStandardDeviation(values, mean);
            double min = StatisticsCalculator.calculateMin(values);
            double max = StatisticsCalculator.calculateMax(values);

            // Create metric entities (interactor's job)
            metrics.add(new ScalarSummaryMetrics(MetricType.MEAN, columnSubset, mean));
            metrics.add(new ScalarSummaryMetrics(MetricType.MEDIAN, columnSubset, median));
            metrics.add(new ScalarSummaryMetrics(MetricType.STANDARD_DEVIATION, columnSubset, stdDev));
            metrics.add(new ScalarSummaryMetrics(MetricType.MIN, columnSubset, min));
            metrics.add(new ScalarSummaryMetrics(MetricType.MAX, columnSubset, max));
            metrics.add(new ScalarSummaryMetrics(MetricType.COUNT, columnSubset, nonNullCount));

        } catch (Exception e) {
            System.err.println("Error calculating metrics for column " + columnName + ": " + e.getMessage());
        }

        return metrics;
    }

    private DataSubsetSpec createColumnSubset(DataSubsetSpec originalSubset, String columnName) {
        return new DataSubsetSpec(
                originalSubset.getSubsetId(),
                List.of(columnName),
                originalSubset.getRowIndices()
        );
    }

    // =================
    // OUTLIER DETECTION
    // =================

    private List<OutlierPoint> detectOutliers(DataSubsetSpec subset, Map<String, List<Double>> numericColumns) {
        List<OutlierPoint> outliers = new ArrayList<>();
        List<String> columnNames = new ArrayList<>(numericColumns.keySet());

        for (int colIdx = 0; colIdx < columnNames.size(); colIdx++) {
            String columnName = columnNames.get(colIdx);
            List<Double> values = numericColumns.get(columnName);

            try {
                if (values == null || values.isEmpty()) {
                    continue;
                }

                // DELEGATE calculation to StatisticsCalculator
                List<StatisticsCalculator.OutlierInfo> outlierInfos =
                        StatisticsCalculator.detectOutliers(values, OUTLIER_Z_SCORE_THRESHOLD);

                // Convert to entity format with colIndex
                List<Integer> rowIndices = subset.getRowIndices();
                for (StatisticsCalculator.OutlierInfo info : outlierInfos) {
                    int actualRowIndex = (rowIndices != null && info.getIndex() < rowIndices.size())
                            ? rowIndices.get(info.getIndex())
                            : info.getIndex();

                    outliers.add(new OutlierPoint(actualRowIndex, colIdx, info.getZScore()));
                }

            } catch (Exception e) {
                System.err.println("Error detecting outliers in column " + columnName + ": " + e.getMessage());
            }
        }

        return outliers;
    }

    // ==================
    // CORRELATION MATRIX
    // ==================

    private double[][] calculateCorrelationMatrix(Map<String, List<Double>> numericColumns, List<String> columnNames) {
        int n = columnNames.size();
        double[][] matrix = new double[n][n];

        // Calculate correlation for each pair
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matrix[i][j] = 1.0;
                } else if (i < j) {
                    // DELEGATE calculation to StatisticsCalculator
                    List<Double> valuesI = numericColumns.get(columnNames.get(i));
                    List<Double> valuesJ = numericColumns.get(columnNames.get(j));

                    double correlation = StatisticsCalculator.calculatePearsonCorrelation(valuesI, valuesJ);
                    matrix[i][j] = correlation;
                    matrix[j][i] = correlation; // Mirror to lower triangle
                }
            }
        }

        return matrix;
    }
}