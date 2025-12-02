package use_case.visualization.io;

import entity.DataSubsetSpec;
import use_case.visualization.model.PlotKind;
import java.util.List;

public class VisualizationInputData {
    private final int summaryReportId;    // or null if no summary
    private final PlotKind type;
    private final DataSubsetSpec subsetSpec;
    private final List<String> xColumns;
    private final List<String> yColumns;
    private final String colorByColumn;  // Optional categorical column for grouping/coloring
    private final String title;

    public VisualizationInputData(int summaryReportId,
        PlotKind type,
        DataSubsetSpec subsetSpec,
        List<String> xColumns,
        List<String> yColumns,
        String title) {
this(summaryReportId, type, subsetSpec, xColumns, yColumns, null, title);
}

    public VisualizationInputData(int summaryReportId,
                                  PlotKind type,
                                  DataSubsetSpec subsetSpec,
                                  List<String> xColumns,
                                  List<String> yColumns,
                                  String colorByColumn,
                                  String title) {
        this.summaryReportId = summaryReportId;
        this.type = type;
        this.subsetSpec = subsetSpec;
        this.xColumns = xColumns;
        this.yColumns = yColumns;
        this.colorByColumn = colorByColumn;
        this.title = title;
    }

    public int getSummaryReportId() { return summaryReportId; }
    public PlotKind getPlotKind() { return type; }
    public DataSubsetSpec getSubsetSpec() { return subsetSpec; }
    public List<String> getXColumns() { return xColumns; }
    public List<String> getYColumns() { return yColumns; }
    public String getColorByColumn() { return colorByColumn; }
    public String getTitle() { return title; }

}
