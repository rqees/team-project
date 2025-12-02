package use_case.table;

import entity.DataSet;
import entity.Column;
import entity.DataRow;
import use_case.dataset.CurrentTableGateway;

import java.util.List;

public class DisplayTableInteractor implements DisplayTableInputBoundary {
    private final CurrentTableGateway tableGateway;
    private final DisplayTableOutputBoundary presenter;

    public DisplayTableInteractor(CurrentTableGateway tableGateway,
                                  DisplayTableOutputBoundary presenter) {
        this.tableGateway = tableGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        DataSet dataSet = tableGateway.load();

        if (dataSet == null) {
            presenter.prepareFailureView("No dataset to display");
            return;
        }

        List<Column> columns = dataSet.getColumns();
        List<DataRow> rows = dataSet.getRows();

        if (columns.isEmpty() || rows.isEmpty()) {
            presenter.prepareFailureView("Dataset is empty");
            return;
        }

        // Convert to view-ready data
        String[] headers = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            headers[i] = columns.get(i).getHeader();
        }

        String[][] rowData = new String[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            List<String> cells = rows.get(i).getCells();
            rowData[i] = cells.toArray(new String[0]);
        }

        final DisplayTableOutputData outputData = new DisplayTableOutputData(headers, rowData);
        presenter.prepareSuccessView(outputData);
    }
}