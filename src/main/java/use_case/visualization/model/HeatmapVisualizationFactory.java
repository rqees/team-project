// package use_case.visualization.model;

// import entity.HeatmapSummaryMetric;
// import entity.MetricType;
// import entity.SummaryMetric;
// import entity.SummaryReport;
// import use_case.visualization.data.DataSubsetData;
// import use_case.visualization.io.VisualizationInputData;

// public class HeatmapVisualizationFactory implements VisualizationModelFactory {

//     @Override
//     public VisualizationModel createModel(VisualizationInputData input,
//                                           DataSubsetData subsetData,
//                                           SummaryReport report) {

//         HeatmapSummaryMetric heatmapMetric = findHeatmapMetric(report);
//         if (heatmapMetric == null) {
//             throw new IllegalStateException("No HeatmapSummaryMetric with MetricType.HEATMAP found in SummaryReport");
//         }

//         Matrix matrix = new Matrix(
//                 heatmapMetric.getHeatmap_values(),
//                 heatmapMetric.getRowLabels(),
//                 heatmapMetric.getColLabels()
//         );

//         return new HeatmapModel(
//                 input.getTitle(),
//                 matrix
//         );
//     }

//     private HeatmapSummaryMetric findHeatmapMetric(SummaryReport report) {
//         for (SummaryMetric metric : report.getSummaryMetrics()) {
//             if (metric instanceof HeatmapSummaryMetric heatmapMetric &&
//                     metric.getMetricType() == MetricType.HEATMAP) {
//                 return heatmapMetric;
//             }
//         }
//         return null;
//     }
// }