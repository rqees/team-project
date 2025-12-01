package interface_adapter.table;

import use_case.table.DisplayTableOutputBoundary;
import use_case.table.DisplayTableOutputData;

public class TablePresenter implements DisplayTableOutputBoundary {
    private final TableViewModel viewModel;

    public TablePresenter(TableViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(DisplayTableOutputData outputData) {
        TableState state = new TableState();
        state.setColumnHeaders(outputData.getHeaders());
        state.setRowData(outputData.getRowData());
        viewModel.setState(state);
    }

    @Override
    public void prepareFailureView(String errorMessage) {
        TableState state = new TableState();
        state.setErrorMessage(errorMessage);
        viewModel.setState(state);
    }
}