package interface_adapter.load_api;

import use_case.load_api.LoadAPIOutputBoundary;

public class LoadAPIPresenter implements LoadAPIOutputBoundary {

    private final LoadAPIViewModel viewModel;

    public LoadAPIPresenter(LoadAPIViewModel viewModel) {
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
