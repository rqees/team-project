package use_case.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.*;


/**
 * Output data for the Summary Statistics Use Case.
 */
public class SummaryStatisticsOutputData {
    // Primitive data extracted from entities
    private final int summaryId;
    private final String reportName;
    private final String datasetId;
    private final int numColumns;
    private final int numRows;
    private final Map<String, ColumnMetricsData> columnMetrics;

    /**
     * Constructor - accepts entity from Interactor.
     * @param report the SummaryReport entity
     */
    public SummaryStatisticsOutputData(SummaryReport report) {
        // Extract primitives from entities
        this.summaryId = report.getSummaryId();
        this.reportName = report.getReportName();

        final DataSubsetSpec subset = report.getSelectedSubset();
        this.datasetId = subset.getSubsetId();
        this.numColumns = subset.getColumnNames().size();
        this.numRows = subset.getRowIndices().size();

        // Extract metrics as primitive data
        this.columnMetrics = extractColumnMetrics(report.getSummaryMetrics());
    }

    /**
     * Extracts column metrics from entity list into primitive data structure.
     * @return Map<String, ColumnMetricsData> column name and metrics associated.
     * @param metrics computed metrics.
     */
    private Map<String, ColumnMetricsData> extractColumnMetrics(List<SummaryMetric> metrics) {
        final Map<String, ColumnMetricsData> result = new HashMap<>();

        for (SummaryMetric metric : metrics) {
            if (!(metric instanceof ScalarSummaryMetrics)) {
                continue;
            }

            final ScalarSummaryMetrics scalarMetric = (ScalarSummaryMetrics) metric;
            final DataSubsetSpec metricSubset = scalarMetric.getSelectedSubset();

            // Scalar metrics for single columns
            if (metricSubset.getColumnNames().size() == 1) {
                final String columnName = metricSubset.getColumnNames().get(0);

                // Get or create data holder
                final ColumnMetricsData data = result.computeIfAbsent(
                        columnName,
                        k -> new ColumnMetricsData(columnName)
                );

                // Store the metric value
                data.setMetric(scalarMetric.getMetricType(), scalarMetric.getMetricValue());
            }
        }

        return result;
    }

    // Getters return primitives only - no entities!
    public int getSummaryId() {
        return summaryId;
    }

    public String getReportName() {
        return reportName;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int getNumRows() {
        return numRows;
    }

    public Map<String, ColumnMetricsData> getColumnMetrics() {
        return columnMetrics;
    }

    /**
     * Simple data class holding column metrics as primitives.
     */
    public static class ColumnMetricsData {
        private final String columnName;
        private double mean;
        private double median;
        private double standardDeviation;
        private double min;
        private double max;
        private long count;

        public ColumnMetricsData(String columnName) {
            this.columnName = columnName;
        }

        public void setMetric(MetricType type, double value) {
            switch (type) {
                case MEAN:
                    this.mean = value;
                    break;
                case MEDIAN:
                    this.median = value;
                    break;
                case STANDARD_DEVIATION:
                    this.standardDeviation = value;
                    break;
                case MIN:
                    this.min = value;
                    break;
                case MAX:
                    this.max = value;
                    break;
                case COUNT:
                    this.count = (long) value;
                    break;
            }
        }

        // Getters return primitives
        public String getColumnName() {
            return columnName;
        }
        public double getMean() {
            return mean;
        }
        public double getMedian() {
            return median;
        }
        public double getStandardDeviation() {
            return standardDeviation;
        }
        public double getMin() {
            return min;
        }
        public double getMax() {
            return max;
        }
        public long getCount() {
            return count;
        }
    }
}
