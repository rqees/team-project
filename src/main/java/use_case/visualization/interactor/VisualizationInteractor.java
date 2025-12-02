// src/main/java/use_case/visualization/interactor/VisualizationInteractor.java
package use_case.visualization.interactor;

import entity.*;

import use_case.visualization.data.DataSubsetData;
import use_case.visualization.gateway.*;
import use_case.visualization.io.*;
import use_case.visualization.model.*;

public class VisualizationInteractor implements VisualizationInputBoundary {

    private final DataSubsetGateway dataSubsetGateway;
    private final SummaryReportGateway summaryReportGateway;
    private final VisualizationOutputBoundary presenter;
    private final PlotKindModelFactory modelFactory;

    public VisualizationInteractor(DataSubsetGateway dataSubsetGateway,
                                   SummaryReportGateway summaryReportGateway,
                                   VisualizationOutputBoundary presenter,
                                   PlotKindModelFactory modelFactory) {
        this.dataSubsetGateway = dataSubsetGateway;
        this.summaryReportGateway = summaryReportGateway;
        this.presenter = presenter;
        this.modelFactory = modelFactory;
    }

    @Override
    public void visualize(VisualizationInputData inputData) {

        DataSubsetSpec subsetSpec = inputData.getSubsetSpec();
        DataSubsetData subsetData = dataSubsetGateway.loadSubset(subsetSpec);

        SummaryReport summaryReport = summaryReportGateway.getById(inputData.getSummaryReportId());

        // 🔥 Explicit Factory usage:
        VisualizationModel model =
                modelFactory.create(inputData, subsetData, summaryReport);

        VisualizationOutputData outputData = new VisualizationOutputData(model);
        presenter.present(outputData);
    }
}