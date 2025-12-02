package interface_adapter.cleaner;

import use_case.cleaner.DataCleaningOutputBoundary;
import use_case.cleaner.DataCleaningOutputData;
import use_case.cleaner.MissingCell;

import java.util.List;

/**
 * Presenter for the Data Cleaning use case.
 **/
public class DataCleaningPresenter implements DataCleaningOutputBoundary{

    private final DataCleaningViewModel dataCleaningViewModel;

    public DataCleaningPresenter(DataCleaningViewModel dataCleaningViewModel) {
        this.dataCleaningViewModel = dataCleaningViewModel;
    }

    public void presentEditedCell(DataCleaningOutputData.EditedCellOutputData outputData) {
        DataCleaningState state = dataCleaningViewModel.getState();

        state.setLastEditedRowIndex(outputData.getRowIndex());
        state.setLastEditedColIndex(outputData.getColIndex());
        state.setLastCleanedValue(outputData.getCleanedValue());
        // clear any old error when edit succeeds
        state.setHeaderErrorMessage(null);

        dataCleaningViewModel.setState(state);
        dataCleaningViewModel.firePropertyChange();
    }

    public void presentHeaderEdit(DataCleaningOutputData.HeaderEditOutputData outputData) {
        DataCleaningState state = dataCleaningViewModel.getState();

        state.setLastEditedHeaderColIndex(outputData.getColIndex());
        state.setLastEditedHeaderValue(outputData.getNewHeader());
        // clear any old error when header edit succeeds
        state.setHeaderErrorMessage(null);

        dataCleaningViewModel.setState(state);
        dataCleaningViewModel.firePropertyChange();
    }

    public void presentHeaderEditFailure(String errorMessage) {
        DataCleaningState state = dataCleaningViewModel.getState();

        state.setHeaderErrorMessage(errorMessage);

        dataCleaningViewModel.setState(state);
        dataCleaningViewModel.firePropertyChange();
    }

    public void presentEntireDataSetCleaned(
            DataCleaningOutputData.CleanEntireDataSetOutputData outputData) {

        List<MissingCell> missingCells = outputData.getMissingCells();

        DataCleaningState state = dataCleaningViewModel.getState();
        state.setMissingCells(missingCells);
        // no header error here
        dataCleaningViewModel.setState(state);
        dataCleaningViewModel.firePropertyChange();
    }

}
