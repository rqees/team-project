package interface_adapter.save_dataset;

import use_case.save_dataset.SaveDataSetOutputBoundary;
import use_case.save_dataset.SaveDataSetOutputData;

/**
 * Presenter that updates the save view model.
 */
public class SaveDataSetPresenter implements SaveDataSetOutputBoundary {

    private final SaveDataSetViewModel viewModel;

    /**
     * Creates a presenter that posts save results to the view model.
     */
    public SaveDataSetPresenter(SaveDataSetViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Publishes the save result to the view model.
     *
     * @param outputData output data describing the save attempt
     */
    @Override
    public void present(SaveDataSetOutputData outputData) {
        viewModel.setMessage(outputData.getMessage());
        viewModel.setSuccess(outputData.isSuccess());
    }
}
