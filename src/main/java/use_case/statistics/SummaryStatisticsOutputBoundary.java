package use_case.statistics;

/**
 * The output boundary for the Summary Statistics Use Case.
 */
public interface SummaryStatisticsOutputBoundary {
    /**
     * Prepares the success view for the Summary Statistics Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(SummaryStatisticsOutputData outputData);

    /**
     * Prepares the failure view for the Summary Statistics Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
