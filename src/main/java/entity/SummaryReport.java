package entity;

public class SummaryReport {
    private final int summary_id;
    private final String report_name;
    private final DataSet dataset;
    private final Column columns_used;
    private final DataRow data_rows_used;

    public SummaryReport(int summary_id, String report_name, Column columns_used, DataRow data_rows_used, DataSet dataset) {
        this.summary_id = summary_id;
        this.report_name = report_name;
        this.columns_used = columns_used;
        this.data_rows_used = data_rows_used;
        this.dataset = dataset;
    }
}
