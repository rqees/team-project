package use_case.visualization.model;

import java.util.List;
import java.util.Map;

public class PointPlotModel implements VisualizationModel {

    private final String title;
    private final PlotKind plotKind;   // SCATTER, LINE, BAR, HISTOGRAM
    private final String xLabel;
    private final String yLabel;
    private final List<DataPoint> dataPoints; // For backward compatibility - first y-column
    private final Map<String, List<DataPoint>> yColumnDataPoints; // Map of y-column name to its data points
    private final List<DataPoint> outliers;
    private final List<Annotation> annotations;

    public PointPlotModel(String title,
                          PlotKind plotKind,
                          String xLabel,
                          String yLabel,
                          List<DataPoint> dataPoints,
                          List<DataPoint> outliers,
                          List<Annotation> annotations) {
        this(title, plotKind, xLabel, yLabel, dataPoints, Map.of(yLabel, dataPoints), outliers, annotations);
    }
    
    public PointPlotModel(String title,
                          PlotKind plotKind,
                          String xLabel,
                          String yLabel,
                          List<DataPoint> dataPoints,
                          Map<String, List<DataPoint>> yColumnDataPoints,
                          List<DataPoint> outliers,
                          List<Annotation> annotations) {
        this.title = title;
        this.plotKind = plotKind;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.dataPoints = dataPoints;
        this.yColumnDataPoints = yColumnDataPoints;
        this.outliers = outliers;
        this.annotations = annotations;
    }

    @Override public String getTitle() { return title; }
    @Override public PlotKind getPlotKind() { return plotKind; }

    public String getXLabel() { return xLabel; }
    public String getYLabel() { return yLabel; }
    public List<DataPoint> getDataPoints() { return dataPoints; }
    public Map<String, List<DataPoint>> getYColumnDataPoints() { return yColumnDataPoints; }
    public List<DataPoint> getOutliers() { return outliers; }
    public List<Annotation> getAnnotations() { return annotations; }
}
