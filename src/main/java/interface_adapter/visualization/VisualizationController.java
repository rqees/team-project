package interface_adapter.visualization;

import entity.DataSubsetSpec;
import use_case.visualization.model.*;
import use_case.visualization.io.*;

import java.util.List;

public class VisualizationController {

    private final VisualizationInputBoundary interactor;

    public VisualizationController(VisualizationInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void onVisualizeButtonClicked(int summaryId,
                                         PlotKind type,
                                         DataSubsetSpec subsetSpec,
                                         List<String> xCols,
                                         List<String> yCols,
                                         String title) {

        VisualizationInputData inputData = new VisualizationInputData(
                summaryId,
                type,
                subsetSpec,
                xCols,
                yCols,
                title
        );

        interactor.visualize(inputData);
    }
}
