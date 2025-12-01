package use_case.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class containing pure statistical calculation methods.
 *
 * This class lives at the USE CASE layer (same level as the interactor).
 * It has no dependencies on entities, data access, or presenters.
 *
 * RESPONSIBILITY: Perform mathematical calculations only.
 * - Follows SRP: Single responsibility is statistical computation
 * - Allows interactor to focus on orchestration/flow
 * - Makes calculations reusable across multiple use cases
 * - Easy to test in isolation
 */
public class StatisticsCalculator {

    /**
     * Calculate the arithmetic mean (average) of a list of values.
     *
     * @param values List of numeric values
     * @return Mean value, or 0.0 if list is empty
     */
    public static double calculateMean(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        return values.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Calculate the median (middle value) of a list of values.
     * For even-sized lists, returns the average of the two middle values.
     *
     * @param values List of numeric values
     * @return Median value, or 0.0 if list is empty
     */
    public static double calculateMedian(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        List<Double> sorted = values.stream()
                .sorted()
                .toList();

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
     *
     * @param values List of numeric values
     * @param mean Pre-calculated mean (for efficiency)
     * @return Standard deviation, or 0.0 if insufficient data
     */
    public static double calculateStandardDeviation(List<Double> values, double mean) {
        if (values == null || values.size() <= 1) {
            return 0.0;
        }

        double sumSquaredDifferences = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .sum();

        return Math.sqrt(sumSquaredDifferences / (values.size() - 1));
    }

    /**
     * Find the minimum value in a list.
     *
     * @param values List of numeric values
     * @return Minimum value, or 0.0 if list is empty
     */
    public static double calculateMin(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        return values.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);
    }

    /**
     * Find the maximum value in a list.
     *
     * @param values List of numeric values
     * @return Maximum value, or 0.0 if list is empty
     */
    public static double calculateMax(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        return values.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
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
    public static double calculateZScore(double value, double mean, double standardDeviation) {
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
    public static boolean isOutlier(double zScore, double threshold) {
        return Math.abs(zScore) > threshold;
    }

    /**
     * Calculate Pearson correlation coefficient between two variables.
     * Returns a value between -1.0 (perfect negative correlation) and
     * +1.0 (perfect positive correlation).
     *
     * @param x First variable values
     * @param y Second variable values
     * @return Correlation coefficient, or 0.0 if calculation not possible
     */
    public static double calculatePearsonCorrelation(List<Double> x, List<Double> y) {
        if (x == null || y == null || x.size() != y.size() || x.isEmpty()) {
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

        if (denominator == 0) {
            return 0.0;
        }

        return numerator / denominator;
    }

    /**
     * Identify outliers in a dataset using z-score method.
     *
     * @param values List of numeric values
     * @param threshold Z-score threshold for outlier detection (typically 3.0)
     * @return List of OutlierInfo objects containing index, value, and z-score
     */
    public static List<OutlierInfo> detectOutliers(List<Double> values, double threshold) {
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
            double value = values.get(i);
            double zScore = calculateZScore(value, mean, stdDev);

            if (isOutlier(zScore, threshold)) {
                outliers.add(new OutlierInfo(i, value, zScore));
            }
        }

        return outliers;
    }

    /**
     * Simple data class to hold outlier information.
     */
    public static class OutlierInfo {
        private final int index;
        private final double value;
        private final double zScore;

        public OutlierInfo(int index, double value, double zScore) {
            this.index = index;
            this.value = value;
            this.zScore = zScore;
        }

        public int getIndex() { return index; }
        public double getValue() { return value; }
        public double getZScore() { return zScore; }
    }
}