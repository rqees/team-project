package use_case.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class containing pure statistical calculation methods.

 * RESPONSIBILITY: Perform mathematical calculations only.
 * - Allows interactor to focus on orchestration
 * - Makes calculations reusable across multiple use cases
 * - Easy to test in isolation
 */
public class StatisticsCalculator {
    /**
     * Calculate the arithmetic mean (average) of a list of values.
     * Null values are excluded from the calculation.
     *
     * @param values List of numeric values (may contain nulls)
     * @return Mean value, or 0.0 if no valid values
     */
    public static double calculateMean(final List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        // Filter out null values
        List<Double> nonNullValues = values.stream()
                .filter(v -> v != null)
                .collect(Collectors.toList());

        if (nonNullValues.isEmpty()) {
            return 0.0;
        }

        return nonNullValues.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Calculate the median (middle value) of a list of values.
     * For even-sized lists, returns the average of the two middle values.
     * Null values are excluded from the calculation.
     *
     * @param values List of numeric values (may contain nulls)
     * @return Median value, or 0.0 if no valid values
     */
    public static double calculateMedian(final List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        // Filter out null values and sort
        List<Double> sorted = values.stream()
                .filter(v -> v != null)
                .sorted()
                .collect(Collectors.toList());

        if (sorted.isEmpty()) {
            return 0.0;
        }

        int size = sorted.size();
        if (size % 2 == 0) {
            // Even count: average of two middle values
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
        } else {
            // Odd count: middle value
            return sorted.get(size / 2);
        }
    }

    /**
     * Calculate the sample standard deviation.
     * Uses n-1 in the denominator (Bessel's correction) for sample data.
     * Null values are excluded from the calculation.
     *
     * @param values List of numeric values (may contain nulls)
     * @param mean Pre-calculated mean (for efficiency)
     * @return Standard deviation, or 0.0 if insufficient valid data
     */
    public static double calculateStandardDeviation(final List<Double> values,
                                                    final double mean) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        // Filter out null values
        List<Double> nonNullValues = values.stream()
                .filter(v -> v != null)
                .collect(Collectors.toList());

        if (nonNullValues.size() <= 1) {
            return 0.0;
        }

        double sumSquaredDifferences = nonNullValues.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .sum();

        return Math.sqrt(sumSquaredDifferences / (nonNullValues.size() - 1));
    }

    /**
     * Find the minimum value in a list.
     * Null values are excluded from the calculation.
     *
     * @param values List of numeric values (may contain nulls)
     * @return Minimum value, or 0.0 if no valid values
     */
    public static double calculateMin(final List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        return values.stream()
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);
    }

    /**
     * Find the maximum value in a list.
     * Null values are excluded from the calculation.
     *
     * @param values List of numeric values (may contain nulls)
     * @return Maximum value, or 0.0 if no valid values
     */
    public static double calculateMax(final List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        return values.stream()
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
    }

    /**
     * Count the number of non-null values in a list.
     *
     * @param values List of numeric values (may contain nulls)
     * @return Count of non-null values
     */
    public static long countNonNull(final List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        return values.stream()
                .filter(v -> v != null)
                .count();
    }

    /**
     * Count the number of null values in a list.
     *
     * @param values List of numeric values (may contain nulls)
     * @return Count of null values (missing data)
     */
    public static long countNull(final List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        return values.stream()
                .filter(v -> v == null)
                .count();
    }

    /**
     * Calculate the z-score for a value.
     * Z-score indicates how many standard deviations a value is from the mean.
     *
     * @param value The value to calculate z-score for
     * @param mean Mean of the dataset
     * @param standardDeviation Standard deviation of the dataset
     * @return Absolute z-score, or 0.0 if standard deviation is 0
     */
    public static double calculateZScore(final double value,
                                         final double mean,
                                         final double standardDeviation) {
        if (standardDeviation == 0) {
            return 0.0;
        }

        return Math.abs((value - mean) / standardDeviation);
    }

    /**
     * Determine if a value is an outlier based on z-score threshold.
     *
     * @param zScore The z-score of the value
     * @param threshold Z-score threshold (typically 3.0)
     * @return true if the value is an outlier
     */
    public static boolean isOutlier(final double zScore,
                                    final double threshold) {
        return Math.abs(zScore) > threshold;
    }

    /**
     * Calculate Pearson correlation coefficient between two variables.
     * Returns a value between -1.0 (perfect negative correlation) and
     * +1.0 (perfect positive correlation).
     *
     * HANDLES MISSING DATA: Uses pairwise deletion.
     * Only pairs where both X and Y are non-null are included.
     *
     * @param x First variable values (may contain nulls)
     *
     * @param y Second variable values (may contain nulls)
     * @return Correlation coefficient, or 0.0 if calculation not possible
     */
    public static double calculatePearsonCorrelation(final List<Double> x, final List<Double> y) {
        if (x == null || y == null || x.size() != y.size() || x.isEmpty()) {
            return 0.0;
        }

        // Pairwise deletion: filter out pairs where either value is null
        List<Double> validX = new ArrayList<>();
        List<Double> validY = new ArrayList<>();

        for (int i = 0; i < x.size(); i++) {
            if (x.get(i) != null && y.get(i) != null) {
                validX.add(x.get(i));
                validY.add(y.get(i));
            }
        }

        if (validX.isEmpty()) {
            return 0.0;
        }

        int n = validX.size();
        double meanX = calculateMean(validX);
        double meanY = calculateMean(validY);

        double numerator = 0.0;
        double sumSquaredDiffX = 0.0;
        double sumSquaredDiffY = 0.0;

        for (int i = 0; i < n; i++) {
            double diffX = validX.get(i) - meanX;
            double diffY = validY.get(i) - meanY;

            numerator += diffX * diffY;
            sumSquaredDiffX += diffX * diffX;
            sumSquaredDiffY += diffY * diffY;
        }

        double denominator = Math.sqrt(sumSquaredDiffX * sumSquaredDiffY);

        if (denominator == 0) {
            return 0.0;
        }

        return numerator / denominator;
    }

    /**
     * Identify outliers in a dataset using z-score method.
     * Null values are excluded from outlier detection.
     * @param values List of numeric values (may contain nulls)
     * @param threshold Z-score threshold for outlier detection (typically 3.0)
     * @return List of OutlierInfo objects containing index, value, and z-score
     */
    public static List<OutlierInfo> detectOutliers(final List<Double> values, final double threshold) {
        List<OutlierInfo> outliers = new ArrayList<>();

        if (values == null || values.isEmpty()) {
            return outliers;
        }

        double mean = calculateMean(values);
        double stdDev = calculateStandardDeviation(values, mean);

        if (stdDev == 0) {
            // No variation in data, no outliers
            return outliers;
        }

        for (int i = 0; i < values.size(); i++) {
            Double value = values.get(i);

            // Skip null values - they are not outliers, they are missing data
            if (value == null) {
                continue;
            }

            double zScore = calculateZScore(value, mean, stdDev);

            if (isOutlier(zScore, threshold)) {
                outliers.add(new OutlierInfo(i, value, zScore));
            }
        }

        return outliers;
    }

    /**
     *
     * Simple data class to hold outlier information.
     */
    public static class OutlierInfo {
        private final int index;
        private final double value;
        private final double zScore;

        public OutlierInfo(final int index, final double value, final double zScore) {
            this.index = index;
            this.value = value;
            this.zScore = zScore;
        }

        public int getIndex() { return index; }
        public double getValue() { return value; }
        public double getZScore() { return zScore; }
    }
}
