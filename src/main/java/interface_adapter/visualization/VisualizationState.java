package interface_adapter.visualization;

import org.knowm.xchart.XYChart;

import java.util.Collections;
import java.util.List;

 /**
  * Immutable snapshot of the visualization to display and column metadata.
  * Fired by VisualizationViewModel via a "state" property change.
  */
public class VisualizationState {

    private final XYChart xyChart;
    private final VisualizationMatrix heatmapMatrix;
    private final String title;
    private final String errorMessage;
    
    // Column metadata
    private final List<String> numericColumnNames;
    private final List<String> categoricalColumnNames;
    private final List<String> allColumnNames;

    public VisualizationState(XYChart xyChart,
                              VisualizationMatrix heatmapMatrix,
                              String title,
                              String errorMessage) {
        this(xyChart, heatmapMatrix, title, errorMessage, 
             Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public VisualizationState(XYChart xyChart,
                              VisualizationMatrix heatmapMatrix,
                              String title,
                              String errorMessage,
                              List<String> numericColumnNames,
                              List<String> categoricalColumnNames,
                              List<String> allColumnNames) {
        this.xyChart = xyChart;
        this.heatmapMatrix = heatmapMatrix;
        this.title = title;
        this.errorMessage = errorMessage;
        this.numericColumnNames = numericColumnNames != null ? numericColumnNames : Collections.emptyList();
        this.categoricalColumnNames = categoricalColumnNames != null ? categoricalColumnNames : Collections.emptyList();
        this.allColumnNames = allColumnNames != null ? allColumnNames : Collections.emptyList();
    }

    public XYChart getXyChart() {
        return xyChart;
    }

    public VisualizationMatrix getHeatmapMatrix() {
        return heatmapMatrix;
    }

    public String getTitle() {
        return title;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasChart() {
        return xyChart != null;
    }

    public boolean hasHeatmap() {
        return heatmapMatrix != null;
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.isBlank();
    }

    /**
     * Get list of numeric column names.
     */
    public List<String> getNumericColumnNames() {
        return numericColumnNames;
    }

    /**
     * Get list of categorical column names.
     */
    public List<String> getCategoricalColumnNames() {
        return categoricalColumnNames;
    }

    /**
     * Get list of all column names.
     */
    public List<String> getAllColumnNames() {
        return allColumnNames;
    }

    /**
     * Check if a column name is numeric.
     */
    public boolean isNumericColumn(String columnName) {
        return numericColumnNames.contains(columnName);
    }

    /**
     * Check if a column name is categorical.
     */
    public boolean isCategoricalColumn(String columnName) {
        return categoricalColumnNames.contains(columnName);
    }
}

