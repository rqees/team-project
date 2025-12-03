package use_case.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import entity.*;
import use_case.visualization.gateway.DataSubsetGateway;
import use_case.visualization.gateway.SummaryReportGateway;
import use_case.visualization.data.DataSubsetData;

/**
 * The Summary Statistics Interactor
 * RESPONSIBILITY: Orchestrate the use case flow only.
 * All calculations are delegated to StatisticsCalculator.
 */
public class SummaryStatisticsInteractor implements SummaryStatisticsInputBoundary {

    private final DataSubsetGateway dataSubsetGateway;
    private final SummaryReportGateway summaryReportGateway;
    private final SummaryStatisticsOutputBoundary summaryStatisticsPresenter;
    private final static double OUTLIER_Z_SCORE_THRESHOLD = 3.0;

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
            final DataSubsetData subsetData;
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
            final List<SummaryMetric> metrics = calculateAllMetrics(subset, subsetData);

            // Step 5: Create SummaryReport entity
            final SummaryReport report = new SummaryReport(
                    input.getDataSubsetId(),
                    input.getReportName(),
                    subset,
                    metrics
            );

            // Step 6: Save the report
            summaryReportGateway.save(report);

            // Step 7: Create and send output data
            final SummaryStatisticsOutputData outputData = new SummaryStatisticsOutputData(report);
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

        if (input.getDataSubsetSpec().getColumnNames() == null
                || input.getDataSubsetSpec().getColumnNames().isEmpty()) {
            throw new IllegalArgumentException("No columns specified for analysis");
        }

        if (input.getReportName() == null
                || input.getReportName().trim().isEmpty()) {
            throw new IllegalArgumentException("Report name is required");
        }
    }

    // ========================================
    // ORCHESTRATION (Application Logic)
    // ========================================

    private List<SummaryMetric> calculateAllMetrics(DataSubsetSpec subset, DataSubsetData subsetData) {
        final List<SummaryMetric> metrics = new ArrayList<>();

        final Map<String, List<Double>> numericColumns = subsetData.getNumericColumns();
        final List<String> columnNames = new ArrayList<>(numericColumns.keySet());

        // Calculate metrics for each numeric column
        for (String columnName : columnNames) {
            metrics.addAll(calculateColumnMetrics(subset, columnName, numericColumns.get(columnName)));
        }

        // Calculate outliers across all numeric columns
        final List<OutlierPoint> outliers = detectOutliers(subset, numericColumns);
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
            final double[][] correlationMatrix = calculateCorrelationMatrix(numericColumns, columnNames);
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
        final List<SummaryMetric> metrics = new ArrayList<>();
        final DataSubsetSpec columnSubset = createColumnSubset(subset, columnName);

        try {
            if (values == null || values.isEmpty()) {
                return metrics;
            }

            // Check if there are any non-null values
            final long nonNullCount = StatisticsCalculator.countNonNull(values);
            if (nonNullCount == 0) {
                // All values are null - skip this column or report as no data
                System.err.println("Warning: Column '" + columnName + "' has no valid numeric values");
                return metrics;
            }

            // DELEGATE ALL CALCULATIONS TO StatisticsCalculator
            final double mean = StatisticsCalculator.calculateMean(values);
            final double median = StatisticsCalculator.calculateMedian(values);
            final double stdDev = StatisticsCalculator.calculateStandardDeviation(values, mean);
            final double min = StatisticsCalculator.calculateMin(values);
            final double max = StatisticsCalculator.calculateMax(values);

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
        final List<OutlierPoint> outliers = new ArrayList<>();
        final List<String> columnNames = new ArrayList<>(numericColumns.keySet());

        for (int colIdx = 0; colIdx < columnNames.size(); colIdx++) {
            final String columnName = columnNames.get(colIdx);
            final List<Double> values = numericColumns.get(columnName);

            try {
                if (values == null || values.isEmpty()) {
                    continue;
                }

                // DELEGATE calculation to StatisticsCalculator
                final List<StatisticsCalculator.OutlierInfo> outlierInfos =
                        StatisticsCalculator.detectOutliers(values, OUTLIER_Z_SCORE_THRESHOLD);

                // Convert to entity format with colIndex
                final List<Integer> rowIndices = subset.getRowIndices();
                for (StatisticsCalculator.OutlierInfo info : outlierInfos) {
                    final int actualRowIndex = (rowIndices != null && info.getIndex() < rowIndices.size())
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
        final int n = columnNames.size();
        final double[][] matrix = new double[n][n];

        // Calculate correlation for each pair
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matrix[i][j] = 1.0;
                } else if (i < j) {
                    // DELEGATE calculation to StatisticsCalculator
                    final List<Double> valuesI = numericColumns.get(columnNames.get(i));
                    final List<Double> valuesJ = numericColumns.get(columnNames.get(j));

                    final double correlation = StatisticsCalculator.calculatePearsonCorrelation(valuesI, valuesJ);
                    matrix[i][j] = correlation;
                    matrix[j][i] = correlation;
                }
            }
        }

        return matrix;
    }
}
