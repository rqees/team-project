package data_access;

import entity.SummaryReport;
import use_case.visualization.gateway.SummaryReportGateway;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in-memory implementation of SummaryReportGateway.
 */
public class InMemorySummaryReportGateway implements SummaryReportGateway {

    private final Map<Integer, SummaryReport> reportsById = new HashMap<>();

    @Override
    public SummaryReport getById(final int summaryId) {
        return reportsById.get(summaryId);
    }

    @Override
    public void save(final SummaryReport report) {
        reportsById.put(report.getSummaryId(), report);
    }
}
