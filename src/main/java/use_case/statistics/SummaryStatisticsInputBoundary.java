package use_case.statistics;

/**
 * Input Boundary for actions which are related to Summary Statistics.
 */

public interface SummaryStatisticsInputBoundary {

    /**
     * Executes the Summary Statistics use case.
     * @param summaryStatisticsInputData the input data
     */
    void execute(SummaryStatisticsInputData summaryStatisticsInputData);
}
