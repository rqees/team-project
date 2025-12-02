package interface_adapter.load_csv;

import use_case.load_csv.LoadOutputBoundary;

public class LoadPresenter implements LoadOutputBoundary {

    private final LoadViewModel viewModel;

    public LoadPresenter(LoadViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareFail(String errorMessage) {
        viewModel.setErrorMessage(errorMessage);
    }

    @Override
    public void prepareSuccess() {
        viewModel.setSuccess(true);
    }
}
