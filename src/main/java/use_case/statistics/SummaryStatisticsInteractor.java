package use_case.statistics;

import entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The Summary Statistics Interactor.
 */
public class SummaryStatisticsInteractor implements SummaryStatisticsInputBoundary {

    private final SummaryStatisticsDataAccessInterface summaryStatisticsDataAccessObject;
    private final SummaryStatisticsOutputBoundary summaryStatisticsPresenter;

    /**
     * @param dataAccess to access DataSubsetSpec objects
     * @param presenter to access  objects
     */
    public SummaryStatisticsInteractor(SummaryStatisticsDataAccessInterface dataAccess,
                                       SummaryStatisticsOutputBoundary presenter) {
        this.summaryStatisticsDataAccessObject = dataAccess;
        this.summaryStatisticsPresenter = presenter;
    }

    @Override
    public void execute(SummaryStatisticsInputData input) {
        try {
            // Validate input
            validateInput(input);

            final DataSubsetSpec subset = input.getDataSubsetSpec();

            // Validate subset exists and is accessible
            if (!summaryStatisticsDataAccessObject.validateDataSubset(subset)) {
                summaryStatisticsPresenter.prepareFailView("Invalid or inaccessible data subset");
                return;
            }

            // Verify dataset exists
            final DataSubsetSpec validatedSubset = summaryStatisticsDataAccessObject.getDataSubsetById(subset.getDatasetId());
            if (validatedSubset == null) {
                summaryStatisticsPresenter.prepareFailView("Dataset not found: " + subset.getDatasetId());
                return;
            }

            // Calculate all summary metrics
            List<SummaryMetric> metrics = calculateAllMetrics(subset);

            // Create SummaryReport entity
            SummaryReport report = new SummaryReport(
                    input.getDataSubsetId(),
                    input.getReportName(),
                    subset,
                    metrics
            );

            // Create and send output data with the report
            SummaryStatisticsOutputData outputData = new SummaryStatisticsOutputData(report);

            summaryStatisticsPresenter.prepareSuccessView(outputData);

        } catch (IllegalArgumentException e) {
            summaryStatisticsPresenter.prepareFailView("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            summaryStatisticsPresenter.prepareFailView("Error computing statistics: " + e.getMessage());
        }
    }

    /**
     * Validates the input data.
     */
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

    /**
     * Calculates all summary metrics for the given data subset.
     */
    private List<SummaryMetric> calculateAllMetrics(DataSubsetSpec subset) {
        List<SummaryMetric> metrics = new ArrayList<>();

        // Separate numeric and non-numeric columns
        List<String> numericColumns = new ArrayList<>();
        for (String columnName : subset.getColumnNames()) {
            if (summaryStatisticsDataAccessObject.isNumericColumn(subset.getDatasetId(), columnName)) {
                numericColumns.add(columnName);
            }
        }

        if (numericColumns.isEmpty()) {
            throw new IllegalArgumentException("No numeric columns found for statistical analysis");
        }

        // Calculate metrics for each numeric column
        for (String columnName : numericColumns) {
            metrics.addAll(calculateColumnMetrics(subset, columnName));
        }

        // Calculate outliers across all numeric columns
        List<OutlierPoint> outliers = detectOutliers(subset, numericColumns);
        final int OUTLIER_Z_SCORE_THRESHOLD = 3;
        if (!outliers.isEmpty()) {
            metrics.add(new OutlierSummaryMetric(
                    MetricType.OUTLIERS,
                    subset,
                    OUTLIER_Z_SCORE_THRESHOLD,
                    outliers
            ));
        }

        // Calculate correlation matrix if multiple numeric columns exist
        if (numericColumns.size() > 1) {
            double[][] correlationMatrix = calculateCorrelationMatrix(subset, numericColumns);
            metrics.add(new CorrelationMatrixMetric(
                    MetricType.CORRELATION_MATRIX,
                    subset,
                    correlationMatrix,
                    numericColumns
            ));
        }

        return metrics;
    }

    /**
     * Calculates all scalar metrics for a single column.
     */
    private List<SummaryMetric> calculateColumnMetrics(DataSubsetSpec subset, String columnName) {
        List<SummaryMetric> metrics = new ArrayList<>();
        DataSubsetSpec columnSubset = createColumnSubset(subset, columnName);

        try {
            List<Double> values = summaryStatisticsDataAccessObject.getNumericColumnValues(subset, columnName);

            if (values.isEmpty()) {
                return metrics; // Return empty list if no data
            }

            // Calculate basic statistics
            double mean = calculateMean(values);
            double median = calculateMedian(values);
            double stdDev = calculateStandardDeviation(values, mean);
            double min = calculateMin(values);
            double max = calculateMax(values);
            int count = values.size();

            // Create metric objects
            metrics.add(new ScalarSummaryMetrics(MetricType.MEAN, columnSubset, mean));
            metrics.add(new ScalarSummaryMetrics(MetricType.MEDIAN, columnSubset, median));
            metrics.add(new ScalarSummaryMetrics(MetricType.STANDARD_DEVIATION, columnSubset, stdDev));
            metrics.add(new ScalarSummaryMetrics(MetricType.MIN, columnSubset, min));
            metrics.add(new ScalarSummaryMetrics(MetricType.MAX, columnSubset, max));
            metrics.add(new ScalarSummaryMetrics(MetricType.COUNT, columnSubset, count));

        } catch (Exception e) {
            // Log error but continue with other columns
            System.err.println("Error calculating metrics for column " + columnName + ": " + e.getMessage());
        }

        return metrics;
    }

    /**
     * Creates a subset specification for a single column.
     */
    private DataSubsetSpec createColumnSubset(DataSubsetSpec originalSubset, String columnName) {
        return new DataSubsetSpec(
                originalSubset.getDatasetId(),
                List.of(columnName),
                originalSubset.getRowIndices()
        );
    }

    /**
     * Calculates the arithmetic mean.
     */
    private double calculateMean(List<Double> values) {
        return values.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Calculates the median.
     */
    private double calculateMedian(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }

        List<Double> sorted = new ArrayList<>(values);
        sorted.sort(Double::compareTo);

        int size = sorted.size();
        if (size % 2 == 0) {
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
        } else {
            return sorted.get(size / 2);
        }
    }

    /**
     * Calculates the sample standard deviation.
     */
    private double calculateStandardDeviation(List<Double> values, double mean) {
        if (values.size() <= 1) {
            return 0.0;
        }

        double sumSquaredDiff = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .sum();

        return Math.sqrt(sumSquaredDiff / (values.size() - 1));
    }

    /**
     * Finds the minimum value.
     */
    private double calculateMin(List<Double> values) {
        return values.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);
    }

    /**
     * Finds the maximum value.
     */
    private double calculateMax(List<Double> values) {
        return values.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
    }

    /**
     * Detects outliers using z-score method across all numeric columns.
     */
    private List<OutlierPoint> detectOutliers(DataSubsetSpec subset, List<String> numericColumns) {
        List<OutlierPoint> outliers = new ArrayList<>();
        final int OUTLIER_Z_SCORE_THRESHOLD = 3;

        for (int colIdx = 0; colIdx < numericColumns.size(); colIdx++) {
            String columnName = numericColumns.get(colIdx);

            try {
                List<Double> values = summaryStatisticsDataAccessObject.getNumericColumnValues(subset, columnName);

                if (values.isEmpty()) {
                    continue;
                }

                double mean = calculateMean(values);
                double stdDev = calculateStandardDeviation(values, mean);

                // Skip if no variation in data
                if (stdDev == 0) {
                    continue;
                }

                // Check each value for outliers
                List<Integer> rowIndices = subset.getRowIndices();
                for (int i = 0; i < values.size(); i++) {
                    double zScore = Math.abs((values.get(i) - mean) / stdDev);

                    if (zScore > OUTLIER_Z_SCORE_THRESHOLD) {
                        int actualRowIndex = (rowIndices != null && i < rowIndices.size())
                                ? rowIndices.get(i)
                                : i;
                        outliers.add(new OutlierPoint(actualRowIndex, colIdx, zScore));
                    }
                }
            } catch (Exception e) {
                // Log and continue with other columns
                System.err.println("Error detecting outliers in column " + columnName + ": " + e.getMessage());
            }
        }

        return outliers;
    }

    /**
     * Calculates the correlation matrix for numeric columns.
     */
    private double[][] calculateCorrelationMatrix(DataSubsetSpec subset, List<String> numericColumns) {
        int n = numericColumns.size();
        double[][] matrix = new double[n][n];

        // Retrieve data for all columns
        List<List<Double>> allColumnValues = new ArrayList<>();
        for (String columnName : numericColumns) {
            try {
                allColumnValues.add(summaryStatisticsDataAccessObject.getNumericColumnValues(subset, columnName));
            } catch (Exception e) {
                // If we can't get data, add empty list
                allColumnValues.add(new ArrayList<>());
            }
        }

        // Calculate correlation for each pair
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matrix[i][j] = 1.0;
                } else if (i < j) {
                    // Calculate only upper triangle
                    double correlation = calculatePearsonCorrelation(
                            allColumnValues.get(i),
                            allColumnValues.get(j)
                    );
                    matrix[i][j] = correlation;
                    matrix[j][i] = correlation; // Mirror to lower triangle
                }
            }
        }

        return matrix;
    }

    /**
     * Calculates Pearson correlation coefficient between two variables.
     */
    private double calculatePearsonCorrelation(List<Double> x, List<Double> y) {
        if (x.size() != y.size() || x.isEmpty()) {
            return 0.0;
        }

        int n = x.size();
        double meanX = calculateMean(x);
        double meanY = calculateMean(y);

        double numerator = 0.0;
        double sumSquaredDiffX = 0.0;
        double sumSquaredDiffY = 0.0;

        for (int i = 0; i < n; i++) {
            double diffX = x.get(i) - meanX;
            double diffY = y.get(i) - meanY;
            numerator += diffX * diffY;
            sumSquaredDiffX += diffX * diffX;
            sumSquaredDiffY += diffY * diffY;
        }

        double denominator = Math.sqrt(sumSquaredDiffX * sumSquaredDiffY);
        return denominator == 0 ? 0.0 : numerator / denominator;
    }
}
