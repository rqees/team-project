package use_case.visualization.gateway;
import entity.SummaryReport;

public interface SummaryReportGateway {
    SummaryReport getById(int summaryId);
}