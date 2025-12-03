package interface_adapter.load_api;

import use_case.load_api.LoadApiOutputBoundary;

public class LoadApiPresenter implements LoadApiOutputBoundary {

    private final LoadApiViewModel viewModel;

    public LoadApiPresenter(LoadApiViewModel viewModel) {
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
