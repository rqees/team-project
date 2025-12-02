package interface_adapter.statistics;

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
            // OutputData contains only primitives - no entities!

            // Create new state
            SummaryStatisticsState state = new SummaryStatisticsState();
            state.setReportName(outputData.getReportName());
            state.setDatasetDescription(buildDatasetDescription(
                    outputData.getDatasetId(),
                    outputData.getNumColumns(),
                    outputData.getNumRows()
            ));
            state.setCalculating(false);
            state.setErrorMessage(null);

            // Format metrics for display
            Map<String, SummaryStatisticsState.ColumnStatistics> columnStats =
                    formatColumnStatistics(outputData.getColumnMetrics());

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

    /**
     * Builds dataset description from primitive data.
     */
    private String buildDatasetDescription(String datasetId, int numColumns, int numRows) {
        return String.format("Dataset: %s (%d columns, %d rows)",
                datasetId, numColumns, numRows);
    }

    /**
     * Formats column metrics from primitive data into view-ready format.
     */
    private Map<String, SummaryStatisticsState.ColumnStatistics> formatColumnStatistics(
            Map<String, SummaryStatisticsOutputData.ColumnMetricsData> columnMetricsData) {

        Map<String, SummaryStatisticsState.ColumnStatistics> result = new LinkedHashMap<>();

        for (Map.Entry<String, SummaryStatisticsOutputData.ColumnMetricsData> entry :
                columnMetricsData.entrySet()) {

            String columnName = entry.getKey();
            SummaryStatisticsOutputData.ColumnMetricsData data = entry.getValue();

            // Format all values for display - all primitives!
            SummaryStatisticsState.ColumnStatistics stats =
                    new SummaryStatisticsState.ColumnStatistics(
                            columnName,
                            formatNumber(data.getMean()),
                            formatNumber(data.getMedian()),
                            formatNumber(data.getStandardDeviation()),
                            formatNumber(data.getMin()),
                            formatNumber(data.getMax()),
                            formatCount(data.getCount())
                    );

            result.put(columnName, stats);
        }

        return result;
    }

    /**
     * Formats a double value for display.
     */
    private String formatNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "N/A";
        }
        return decimalFormat.format(value);
    }

    /**
     * Formats a count value for display.
     */
    private String formatCount(long value) {
        return String.valueOf(value);
    }
}