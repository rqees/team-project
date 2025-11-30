package interface_adapter.table;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import use_case.table.DisplayTableOutputBoundary;

import java.util.List;

public class TablePresenter implements DisplayTableOutputBoundary {
    private final TableViewModel viewModel;

    public TablePresenter(TableViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentTable(DataSet dataSet) {
        TableState state = new TableState();

        if (dataSet == null) {
            state.setErrorMessage("No dataset to display");
            viewModel.setState(state);
            return;
        }

        List<Column> columns = dataSet.getColumns();
        List<DataRow> rows = dataSet.getRows();

        if (columns.isEmpty() || rows.isEmpty()) {
            state.setErrorMessage("Dataset is empty");
            viewModel.setState(state);
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

        state.setColumnHeaders(headers);
        state.setRowData(rowData);
        viewModel.setState(state);
    }
}