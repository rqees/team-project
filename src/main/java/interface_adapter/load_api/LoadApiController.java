package interface_adapter.load_api;

import use_case.load_api.LoadApiInputBoundary;
import use_case.load_api.LoadApiInputData;

public class LoadApiController {
    private final LoadApiInputBoundary loadApiInteractor;

    public LoadApiController(LoadApiInputBoundary loadApiInteractor) {
        this.loadApiInteractor = loadApiInteractor;
    }

    /**
     * Executes the Load API use case.
     * @param name name of the dataset
     */
    public void execute(String name) {
        final LoadApiInputData loadApiInputData = new LoadApiInputData(name);
        loadApiInteractor.execute(loadApiInputData);
    }
}
