package use_case.visualization.model;

import entity.SummaryReport;
import use_case.visualization.data.DataSubsetData;
import use_case.visualization.io.VisualizationInputData;

import java.util.EnumMap;
import java.util.Map;

public class PlotKindModelFactory {

    private final Map<PlotKind, VisualizationModelFactory> factories =
            new EnumMap<>(PlotKind.class);

    public PlotKindModelFactory() {
        VisualizationModelFactory pointFactory = new PointPlotVisualizationFactory();
        VisualizationModelFactory heatmapFactory = new HeatmapVisualizationFactory();

        factories.put(PlotKind.SCATTER, pointFactory);
        factories.put(PlotKind.LINE, pointFactory);
        factories.put(PlotKind.BAR, pointFactory);
        factories.put(PlotKind.HISTOGRAM, pointFactory);
        factories.put(PlotKind.HEATMAP, heatmapFactory);
    }

    public VisualizationModel create(VisualizationInputData input,
                                     DataSubsetData subsetData,
                                     SummaryReport report) {
        VisualizationModelFactory factory = factories.get(input.getPlotKind());
        if (factory == null) {
            throw new IllegalArgumentException("No VisualizationModelFactory registered for plot kind: " + input.getPlotKind());
        }
        return factory.createModel(input, subsetData, report);
    }
}