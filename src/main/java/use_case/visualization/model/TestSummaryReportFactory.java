package use_case.visualization.model;

import entity.*;
import use_case.statistics.StatisticsCalculator;
import use_case.visualization.data.DataSubsetData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Factory for creating test SummaryReport instances from DataSubsetData.
 * This is used by the visualization use case when no real summary report exists,
 * allowing the visualization to overlay summary metrics (mean, median, min, max, outliers, etc.)
 * based on the actual data being visualized.
 */
public class TestSummaryReportFactory {
    
    private static final double OUTLIER_Z_SCORE_THRESHOLD = 3.0;
    
    /**
     * Creates a test SummaryReport from the given DataSubsetData.
     * This report contains all the standard metrics (mean, median, std dev, min, max, outliers)
     * calculated from the actual data, allowing visualization to overlay these metrics.
     * 
     * @param subsetSpec the data subset specification
     * @param subsetData the actual numeric data
     * @return a SummaryReport with calculated metrics
     */
    public static SummaryReport createTestReport(DataSubsetSpec subsetSpec, DataSubsetData subsetData) {
        List<SummaryMetric> metrics = new ArrayList<>();
        
        Map<String, List<Double>> numericColumns = subsetData.getNumericColumns();
        List<String> columnNames = new ArrayList<>(numericColumns.keySet());
        
        // Calculate metrics for each numeric column
        for (String columnName : columnNames) {
            List<Double> values = numericColumns.get(columnName);
            if (values == null || values.isEmpty()) {
                continue;
            }
            
            // Check if there are any non-null values
            long nonNullCount = StatisticsCalculator.countNonNull(values);
            if (nonNullCount == 0) {
                continue;
            }
            
            // Create column-specific subset
            DataSubsetSpec columnSubset = new DataSubsetSpec(
                    subsetSpec.getSubsetId(),
                    List.of(columnName),
                    subsetSpec.getRowIndices()
            );
            
            // Calculate all scalar metrics
            double mean = StatisticsCalculator.calculateMean(values);
            double median = StatisticsCalculator.calculateMedian(values);
            double stdDev = StatisticsCalculator.calculateStandardDeviation(values, mean);
            double min = StatisticsCalculator.calculateMin(values);
            double max = StatisticsCalculator.calculateMax(values);
            
            // Add scalar metrics
            metrics.add(new ScalarSummaryMetrics(MetricType.MEAN, columnSubset, mean));
            metrics.add(new ScalarSummaryMetrics(MetricType.MEDIAN, columnSubset, median));
            metrics.add(new ScalarSummaryMetrics(MetricType.STANDARD_DEVIATION, columnSubset, stdDev));
            metrics.add(new ScalarSummaryMetrics(MetricType.MIN, columnSubset, min));
            metrics.add(new ScalarSummaryMetrics(MetricType.MAX, columnSubset, max));
            metrics.add(new ScalarSummaryMetrics(MetricType.COUNT, columnSubset, nonNullCount));
        }
        
        // Calculate outliers across all numeric columns
        List<OutlierPoint> outliers = detectOutliers(subsetSpec, numericColumns, columnNames);
        if (!outliers.isEmpty()) {
            metrics.add(new OutlierSummaryMetric(
                    MetricType.OUTLIERS,
                    subsetSpec,
                    OUTLIER_Z_SCORE_THRESHOLD,
                    outliers
            ));
        }
        
        // Calculate correlation matrix if multiple numeric columns exist
        if (columnNames.size() > 1) {
            double[][] correlationMatrix = calculateCorrelationMatrix(numericColumns, columnNames);
            metrics.add(new CorrelationMatrixMetric(
                    MetricType.CORRELATION_MATRIX,
                    subsetSpec,
                    correlationMatrix,
                    columnNames
            ));
        }
        
        // Create and return test report
        return new SummaryReport(
                -1, // Test report ID
                "Test Summary Report",
                subsetSpec,
                metrics
        );
    }
    
    private static List<OutlierPoint> detectOutliers(DataSubsetSpec subset, 
                                                     Map<String, List<Double>> numericColumns,
                                                     List<String> columnNames) {
        List<OutlierPoint> outliers = new ArrayList<>();
        
        for (int colIdx = 0; colIdx < columnNames.size(); colIdx++) {
            String columnName = columnNames.get(colIdx);
            List<Double> values = numericColumns.get(columnName);
            
            if (values == null || values.isEmpty()) {
                continue;
            }
            
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
        }
        
        return outliers;
    }
    
    private static double[][] calculateCorrelationMatrix(Map<String, List<Double>> numericColumns, 
                                                          List<String> columnNames) {
        int n = columnNames.size();
        double[][] matrix = new double[n][n];
        
        // Calculate correlation for each pair
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matrix[i][j] = 1.0;
                } else if (i < j) {
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

