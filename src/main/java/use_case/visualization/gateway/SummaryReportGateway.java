package use_case.visualization.gateway;

import entity.SummaryReport;

/**
 * Gateway for storing and retrieving SummaryReport entities.
 * Statistics use case will save reports; visualization will load them.
 */
public interface SummaryReportGateway {

    /**
     * Retrieve a SummaryReport by its id.
     *
     * @param summaryId the id of the summary report
     * @return the SummaryReport, or null if not found
     */
    SummaryReport getById(int summaryId);

    /**
     * Save or update a SummaryReport.
     *
     * @param report the report to save
     */
    void save(SummaryReport report);
}
