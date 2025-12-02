package interface_adapter.statistics;

import java.util.Map;

/**
 * State class for Summary Statistics ViewModel.
 */
public class SummaryStatisticsState {

    private String reportName = "";
    private String datasetDescription = "";
    private Map<String, ColumnStatistics> columnStats = null;
    private String errorMessage = null;
    private boolean isCalculating = false;

    // Copy constructor
    public SummaryStatisticsState(SummaryStatisticsState copy) {
        this.reportName = copy.reportName;
        this.datasetDescription = copy.datasetDescription;
        this.columnStats = copy.columnStats;
        this.errorMessage = copy.errorMessage;
        this.isCalculating = copy.isCalculating;
    }

    public SummaryStatisticsState() {
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getDatasetDescription() {
        return datasetDescription;
    }

    public void setDatasetDescription(String datasetDescription) {
        this.datasetDescription = datasetDescription;
    }

    public Map<String, ColumnStatistics> getColumnStats() {
        return columnStats;
    }

    public void setColumnStats(Map<String, ColumnStatistics> columnStats) {
        this.columnStats = columnStats;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isCalculating() {
        return isCalculating;
    }

    public void setCalculating(boolean calculating) {
        isCalculating = calculating;
    }

    /**
     * Statistics for a single column.
     */
    public static class ColumnStatistics {
        private final String columnName;
        private final String mean;
        private final String median;
        private final String standardDeviation;
        private final String min;
        private final String max;
        private final String count;

        public ColumnStatistics(String columnName, String mean, String median,
                                String standardDeviation, String min, String max, String count) {
            this.columnName = columnName;
            this.mean = mean;
            this.median = median;
            this.standardDeviation = standardDeviation;
            this.min = min;
            this.max = max;
            this.count = count;
        }

        public String getColumnName() { return columnName; }
        public String getMean() { return mean; }
        public String getMedian() { return median; }
        public String getStandardDeviation() { return standardDeviation; }
        public String getMin() { return min; }
        public String getMax() { return max; }
        public String getCount() { return count; }
    }
}
