package use_case.visualization.model;

import entity.SummaryReport;
import use_case.visualization.data.DataSubsetData;
import use_case.visualization.io.VisualizationInputData;

public interface VisualizationModelFactory {

    // create a VisualizationModel for the given input, subset data, and summary report.

    // the factory in my implementation of "Factory" design pattern

    VisualizationModel createModel(VisualizationInputData input,
                                   DataSubsetData subsetData,
                                   SummaryReport report);
}