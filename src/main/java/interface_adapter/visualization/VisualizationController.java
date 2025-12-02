
package interface_adapter.visualization;

import entity.Column;
import entity.DataSet;
import entity.DataSubsetSpec;
import entity.DataType;
import use_case.dataset.CurrentTableGateway;
import use_case.visualization.io.VisualizationInputBoundary;
import use_case.visualization.io.VisualizationInputData;
import use_case.visualization.io.VisualizationOutputBoundary;
import use_case.visualization.model.PlotKind;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Visualization use case.
 * The View calls this with primitive data (strings, lists, enums, row indices).
 * The controller is responsible for constructing DataSubsetSpec and VisualizationInputData.
 * Controllers handle commands only - they do not expose query methods.
 */
public class VisualizationController {

    private final VisualizationInputBoundary interactor;
    private final CurrentTableGateway tableGateway;
    private final VisualizationOutputBoundary presenter;

    public VisualizationController(VisualizationInputBoundary interactor, 
                                  CurrentTableGateway tableGateway,
                                  VisualizationOutputBoundary presenter) {
        this.interactor = interactor;
        this.tableGateway = tableGateway;
        this.presenter = presenter;
    }

    /**
     * Legacy method for backward compatibility. Prefer using visualizeWithPrimitiveData.
     */
    public void visualize(VisualizationInputData inputData) {
        interactor.visualize(inputData);
    }

    /**
     * Visualize with primitive data. The controller constructs DataSubsetSpec and VisualizationInputData.
     * 
     * @param plotKindView The type of plot to create (view-layer enum)
     * @param selectedColumnIndices Column indices selected by the user (0-based)
     * @param xAxisColumnName Name of the X-axis column
     * @param yColumnNames List of Y-axis column names
     * @param colorByColumnName Optional categorical column name for coloring (can be null)
     * @param rowIndices Row indices to include (0-based). If null, includes all rows.
     */
    public void visualizeWithPrimitiveData(PlotKindView plotKindView,
                                           List<Integer> selectedColumnIndices,
                                           String xAxisColumnName,
                                           List<String> yColumnNames,
                                           String colorByColumnName,
                                           List<Integer> rowIndices) {
        // Map view-layer enum to use case enum
        PlotKind plotKind = mapPlotKindViewToPlotKind(plotKindView);
        if (tableGateway == null) {
            throw new IllegalStateException("Table gateway not available");
        }

        DataSet dataSet = tableGateway.load();
        if (dataSet == null) {
            throw new IllegalStateException("No dataset loaded");
        }

        // Convert column indices to column names
        List<String> columnNames = new ArrayList<>();
        for (Integer colIndex : selectedColumnIndices) {
            if (colIndex >= 0 && colIndex < dataSet.getColumns().size()) {
                columnNames.add(dataSet.getColumns().get(colIndex).getHeader());
            }
        }

        // Ensure X-axis, Y-axis, and color-by columns are included
        if (!columnNames.contains(xAxisColumnName)) {
            columnNames.add(xAxisColumnName);
        }
        for (String yCol : yColumnNames) {
            if (!columnNames.contains(yCol)) {
                columnNames.add(yCol);
            }
        }
        if (colorByColumnName != null && !columnNames.contains(colorByColumnName)) {
            columnNames.add(colorByColumnName);
        }

        // Use all rows if rowIndices is null
        List<Integer> finalRowIndices = rowIndices != null ? rowIndices : getAllRowIndices(dataSet);

        // Create DataSubsetSpec
        DataSubsetSpec subsetSpec = new DataSubsetSpec("visualization-subset", columnNames, finalRowIndices);

        // Create title (use view enum name for display)
        String title = plotKindView.name() + ": " + xAxisColumnName;
        if (!yColumnNames.isEmpty()) {
            title += " vs " + String.join(", ", yColumnNames);
        }
        if (colorByColumnName != null) {
            title += " (by " + colorByColumnName + ")";
        }

        // Create VisualizationInputData
        VisualizationInputData inputData = new VisualizationInputData(
                -1, // summaryReportId
                plotKind,
                subsetSpec,
                List.of(xAxisColumnName),
                yColumnNames,
                colorByColumnName,
                title
        );

        // Execute visualization
        interactor.visualize(inputData);
    }

    /**
     * Command to update column metadata in the ViewModel.
     * Called when table data changes.
     */
    public void updateColumnMetadata() {
        if (tableGateway == null || presenter == null) {
            return;
        }

        DataSet dataSet = tableGateway.load();
        if (dataSet == null) {
            // Clear metadata if no dataset
            if (presenter instanceof VisualizationPresenter) {
                ((VisualizationPresenter) presenter).updateColumnMetadata(
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()
                );
            }
            return;
        }

        List<String> numericColumns = new ArrayList<>();
        List<String> categoricalColumns = new ArrayList<>();
        List<String> allColumns = new ArrayList<>();

        for (Column column : dataSet.getColumns()) {
            String header = column.getHeader();
            allColumns.add(header);
            if (column.getDataType() == DataType.NUMERIC) {
                numericColumns.add(header);
            } else if (column.getDataType() == DataType.CATEGORICAL) {
                categoricalColumns.add(header);
            }
        }

        if (presenter instanceof VisualizationPresenter) {
            ((VisualizationPresenter) presenter).updateColumnMetadata(
                numericColumns,
                categoricalColumns,
                allColumns
            );
        }
    }

    /**
     * Map PlotKindView (view-layer enum) to PlotKind (use case enum).
     * This is the only place where the mapping between view and use case enums occurs.
     */
    private PlotKind mapPlotKindViewToPlotKind(PlotKindView plotKindView) {
        return switch (plotKindView) {
            case SCATTER -> PlotKind.SCATTER;
            case LINE -> PlotKind.LINE;
            case BAR -> PlotKind.BAR;
            case HISTOGRAM -> PlotKind.HISTOGRAM;
            case HEATMAP -> PlotKind.HEATMAP;
        };
    }

    /**
     * Get all row indices for the current dataset.
     */
    private List<Integer> getAllRowIndices(DataSet dataSet) {
        int rowCount = dataSet.getRows().size();
        List<Integer> rowIndices = new ArrayList<>(rowCount);
        for (int i = 0; i < rowCount; i++) {
            rowIndices.add(i);
        }
        return rowIndices;
    }
}

