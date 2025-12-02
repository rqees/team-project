package interface_adapter.load_api;

import use_case.load_api.LoadAPIInputBoundary;
import use_case.load_api.LoadAPIInputData;

public class LoadAPIController {
    private final LoadAPIInputBoundary loadAPIInteractor;

    public LoadAPIController(LoadAPIInputBoundary loadAPIInteractor) {
        this.loadAPIInteractor = loadAPIInteractor;
    }

    public void execute(String name) {
        LoadAPIInputData loadAPIInputData = new LoadAPIInputData(name);
        loadAPIInteractor.execute(loadAPIInputData);
    }
}
