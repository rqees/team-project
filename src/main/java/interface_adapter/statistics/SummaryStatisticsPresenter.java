package interface_adapter.statistics;

import entity.*;
import use_case.statistics.SummaryStatisticsOutputBoundary;
import use_case.statistics.SummaryStatisticsOutputData;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Presenter for Summary Statistics
 */
public class SummaryStatisticsPresenter implements SummaryStatisticsOutputBoundary {

    private final SummaryStatisticsViewModel viewModel;
    private final DecimalFormat decimalFormat;

    public SummaryStatisticsPresenter(SummaryStatisticsViewModel viewModel) {
        this.viewModel = viewModel;
        this.decimalFormat = new DecimalFormat("#,##0.00");
    }

    @Override
    public void prepareSuccessView(SummaryStatisticsOutputData outputData) {
        try {
            SummaryReport report = outputData.getReport();

            // Create new state
            SummaryStatisticsState state = new SummaryStatisticsState();
            state.setReportName(report.getReportName());
            state.setDatasetDescription(buildDatasetDescription(report.getSelectedSubset()));
            state.setCalculating(false);
            state.setErrorMessage(null);

            // Organize and format metrics
            Map<String, ColumnMetricsHolder> columnMetricsMap = organizeMetricsByColumn(
                    report.getSummaryMetrics()
            );
            Map<String, SummaryStatisticsState.ColumnStatistics> columnStats =
                    formatColumnStatistics(columnMetricsMap);

            state.setColumnStats(columnStats);

            // Update ViewModel - this will notify the View
            viewModel.setState(state);

        } catch (Exception e) {
            prepareFailView("Error preparing statistics display: " + e.getMessage());
        }
    }

    @Override
    public void prepareFailView(String errorMessage) {
        SummaryStatisticsState state = new SummaryStatisticsState();
        state.setErrorMessage(errorMessage);
        state.setCalculating(false);
        viewModel.setState(state);
    }

    /**
     * Sets the calculating state.
     */
    public void prepareCalculatingView() {
        SummaryStatisticsState state = new SummaryStatisticsState(viewModel.getState());
        state.setCalculating(true);
        state.setErrorMessage(null);
        viewModel.setState(state);
    }

    private String buildDatasetDescription(DataSubsetSpec subset) {
        int numColumns = subset.getColumnNames().size();
        int numRows = subset.getRowIndices().size();
        return String.format("Dataset: %s (%d columns, %d rows)",
                subset.getSubsetId(), numColumns, numRows);
    }

    private Map<String, ColumnMetricsHolder> organizeMetricsByColumn(List<SummaryMetric> metrics) {
        Map<String, ColumnMetricsHolder> columnMetrics = new LinkedHashMap<>();

        for (SummaryMetric metric : metrics) {
            if (!(metric instanceof ScalarSummaryMetrics)) {
                continue;
            }

            ScalarSummaryMetrics scalarMetric = (ScalarSummaryMetrics) metric;
            DataSubsetSpec subset = scalarMetric.getSelectedSubset();

            if (subset.getColumnNames().size() == 1) {
                String columnName = subset.getColumnNames().get(0);

                ColumnMetricsHolder holder = columnMetrics.computeIfAbsent(
                        columnName,
                        k -> new ColumnMetricsHolder(columnName)
                );

                holder.setMetric(scalarMetric.getMetricType(), scalarMetric.getMetricValue());
            }
        }

        return columnMetrics;
    }

    private Map<String, SummaryStatisticsState.ColumnStatistics> formatColumnStatistics(
            Map<String, ColumnMetricsHolder> columnMetricsMap) {

        Map<String, SummaryStatisticsState.ColumnStatistics> result = new LinkedHashMap<>();

        for (Map.Entry<String, ColumnMetricsHolder> entry : columnMetricsMap.entrySet()) {
            String columnName = entry.getKey();
            ColumnMetricsHolder holder = entry.getValue();

            SummaryStatisticsState.ColumnStatistics stats =
                    new SummaryStatisticsState.ColumnStatistics(
                            columnName,
                            formatNumber(holder.getMean()),
                            formatNumber(holder.getMedian()),
                            formatNumber(holder.getStandardDeviation()),
                            formatNumber(holder.getMin()),
                            formatNumber(holder.getMax()),
                            formatCount(holder.getCount())
                    );

            result.put(columnName, stats);
        }

        return result;
    }

    private String formatNumber(Double value) {
        if (value == null) {
            return "N/A";
        }
        return decimalFormat.format(value);
    }

    private String formatCount(Double value) {
        if (value == null) {
            return "0";
        }
        return String.valueOf(value.intValue());
    }

    /**
     * Helper class to hold all metrics for a single column.
     */
    private static class ColumnMetricsHolder {
        private final String columnName;
        private Double mean;
        private Double median;
        private Double standardDeviation;
        private Double min;
        private Double max;
        private Double count;

        public ColumnMetricsHolder(String columnName) {
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
                    this.count = value;
                    break;
                default:
                    break;
            }
        }

        public String getColumnName() { return columnName; }
        public Double getMean() { return mean; }
        public Double getMedian() { return median; }
        public Double getStandardDeviation() { return standardDeviation; }
        public Double getMin() { return min; }
        public Double getMax() { return max; }
        public Double getCount() { return count; }
    }
}