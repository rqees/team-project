package interface_adapter.table;

import use_case.table.DisplayTableInputBoundary;

public class TableController {
    private final DisplayTableInputBoundary displayTableInteractor;

    public TableController(DisplayTableInputBoundary displayTableInteractor) {
        this.displayTableInteractor = displayTableInteractor;
    }

    public void displayCurrentTable() {
        displayTableInteractor.execute();
    }
}