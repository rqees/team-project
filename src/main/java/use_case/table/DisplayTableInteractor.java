package use_case.table;

import entity.DataSet;
import use_case.dataset.CurrentTableGateway;

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
        presenter.presentTable(dataSet);
    }
}